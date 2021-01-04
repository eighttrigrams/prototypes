require 'spec_helper'



# @author: Daniel M. de Oliveira
#
describe NoCloud::Grid do

  TODOFILENAME='persistentstringqueue.csv'
  LOCALHOSTIDENTIFIER='devHost'
  REMOTEHOSTIDENTIFIER='remoteHost1'
  COMMAND_DISTRIBUTE='DISTRIBUTE'
  COMMA=','

  before :all do
    createTestDirs
  end
  
  after :all do
    removeTestDirs
  end

  before :each do
    @md5 = Digest::MD5.file(TESTDIR+TESTFILENAME).hexdigest 
    
    @fileNotFoundMsg='No such file or directory: '
    #@sleepTimeForReplication = 0.05


    @fs2 = double NoCloud::Connectors::RsyncSshStorageConnector
    allow(@fs2).to receive('respond_to?') { true }
    allow(@fs2).to receive('setStorageLocation')

    ln=NoCloud::Node.new LOCALHOSTIDENTIFIER
    ln.storage_root_path=TESTDIR+'storage'

    rn=NoCloud::Node.new REMOTEHOSTIDENTIFIER

    @g = NoCloud::Grid.new(ln,DBLOCATION)
    @g.addNode @fs2,rn
  end
  
  after :each do
    FileUtils.remove TESTDIR + FETCHEDFILENAME if File.exists? TESTDIR + FETCHEDFILENAME
    FileUtils.remove DBLOCATION + RECORDFILENAME if File.exists? DBLOCATION + RECORDFILENAME
    FileUtils.remove DBLOCATION + TODOFILENAME if File.exist? DBLOCATION + TODOFILENAME
  end

  it 'should raise FileNotFoundError when storageLocation not exists' do
    n=NoCloud::Node.new(LOCALHOSTIDENTIFIER, NOTEXISTINGDIR)

    expect{NoCloud::Grid.new n, DBLOCATION}.to raise_error NoCloud::Errors::FileNotFoundError
  end

  it 'should raise an error if file not exists' do
    expect{@g.distribute NOTEXISTINGFILENAME,'notexisting.txt.logical'}.to raise_error(NoCloud::Errors::FileNotFoundError,
      @fileNotFoundMsg+NOTEXISTINGFILENAME)
  end

  it 'should return true if file has been stored locally' do
    expect(@g.distribute TESTDIR + TESTFILENAME, TESTFILENAME).to be true
    expect(File.exists? OWNEDLOCATION+TESTFILENAME).to be true
  end

  it 'should also add todos for sending the files to other connectors' do
    @g.distribute TESTDIR + TESTFILENAME, TESTFILENAME
    contents,lc=readFile(DBLOCATION + TODOFILENAME)
    expect(contents[0].include? COMMAND_DISTRIBUTE+COMMA+REMOTEHOSTIDENTIFIER+COMMA+TESTFILENAME).to be true
  end

  it 'should let users retrieve a file after storing it' do
    
    @g.distribute TESTDIR + TESTFILENAME, TESTFILENAME
    @g.fetch TESTFILENAME, TESTDIR+FETCHEDFILENAME
    expect(File.exists? TESTDIR+FETCHEDFILENAME).to be true
  end

  it 'should raise FileNotFoundError if file to fetch not found' do   
    allow(@fs2).to receive(:fetch) { false }
    allow(@fs2).to receive(:exist?) { false }
    expect{@g.fetch(NOTEXISTINGFILENAME, '/tmp/'+NOTEXISTINGFILENAME)}.to raise_error NoCloud::Errors::FileNotFoundError, @fileNotFoundMsg+NOTEXISTINGFILENAME
  end

  it 'should ask another endpoint if file not found locally' do
    @g.distribute TESTDIR + TESTFILENAME, TESTFILENAME
    FileUtils.remove OWNEDLOCATION+TESTFILENAME
    expect(@fs2).to receive(:fetch).with(LOCALHOSTIDENTIFIER+NoCloud::FS_SEPARATOR+TESTFILENAME,FETCHEDFILENAME) { true }
    expect(@fs2).to receive(:exist?) { true }
    expect(@g.fetch(TESTFILENAME,FETCHEDFILENAME))
  end

  it 'should raise FileNotFoundError if target directory for fetch not found' do
    
    @g.distribute TESTDIR + TESTFILENAME, TESTFILENAME
    expect{@g.fetch(TESTFILENAME,NOTEXISTINGDIR+FETCHEDFILENAME)}.to raise_error NoCloud::Errors::FileNotFoundError, 'No such file or directory: '+NOTEXISTINGDIR+FETCHEDFILENAME
  end
  
  it 'should have a method issValid' do
    expect(@g.respond_to? :isValid).to be true
  end
  
  it 'should respond that is not valid if no record found' do
    expect(@g.isValid(TESTFILENAME,DEFAULTCHECKSUMTIMEOUT)).to be false
  end
  
  it 'should be invalid if no record for remote location' do
    @g.distribute TESTDIR + TESTFILENAME, TESTFILENAME
    expect(@g.isValid(TESTFILENAME,DEFAULTCHECKSUMTIMEOUT)).to be false
  end
  
  it 'should be valid when records present and valid' do
    @g.distribute TESTDIR + TESTFILENAME, TESTFILENAME
    contents=[]
    contents<<Time.now.to_s+',remoteHost1,'+TESTFILENAME+',md5,d41d8cd98f00b204e9800998ecf8427e'
    contents<<Time.now.to_s+',devHost,'+TESTFILENAME+',md5,d41d8cd98f00b204e9800998ecf8427e'
    writeFile(DBLOCATION+RECORDFILENAME,contents)
    expect(@g.isValid(TESTFILENAME,DEFAULTCHECKSUMTIMEOUT)).to be true
  end
  
  it 'should be invalid when remote checksum too old' do
    @g.distribute TESTDIR + TESTFILENAME, TESTFILENAME
    contents=[]
    contents<<Time.now.to_s+',devHost,'+TESTFILENAME+',md5,d41d8cd98f00b204e9800998ecf8427e'
    contents<<'2015-02-12 17:41:41 +0100,remoteHost1,'+TESTFILENAME+',md5,d41d8cd98f00b204e9800998ecf8427e' # simulate @g.gather
    writeFile(DBLOCATION+RECORDFILENAME,contents)
    expect(@g.isValid(TESTFILENAME,2)).to be false
  end
  
  it 'should generate a custody metadata record' do
    FileUtils.cp TESTDIR+TESTFILENAME,REMOTEHOST1LOCATION+TESTFILENAME
    @g.refreshStorageChecksums
    expect(File.exist? REMOTEHOST1LOCATION + RECORDFILENAME).to be true
    contents,lineCount=readFile(REMOTEHOST1LOCATION+RECORDFILENAME)
    expect(contents[0].include? @md5).to be true
  end
end

