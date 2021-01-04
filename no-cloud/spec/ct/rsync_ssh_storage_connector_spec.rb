require_relative '../spec_helper'
 

# @author: Daniel M. de Oliveira
#
describe NoCloud::Connectors::RsyncSshStorageConnector do
  
  USER='vagrant'
  PORT='2225'
  NOTEXISTENTPORT='2219'
  HOST='127.0.0.1'
  RETRIEVEDFILENAME=TESTFILENAME+'.retrieved'
  TARGETLOCATION='/tmp' # SHOULD NOT END WITH SEPARATOR

  CMD="ssh -p %{port} %{user}@%{host} 'if [ -e %{storageLocation}/%{storedFileName} ]; then rm %{storageLocation}/%{storedFileName}; fi'"

  before :all do
	 createTestDirs
  end

  after :all do
	 removeTestDirs
  end

  before :each do    
    @sc=NoCloud::Connectors::RsyncSshStorageConnector.new(USER,HOST,PORT)
    @sc.setStorageLocation TARGETLOCATION
  end
  
  after :each do
    FileUtils.remove TESTDIR+RETRIEVEDFILENAME if File.exist? TESTDIR+RETRIEVEDFILENAME
    `#{CMD % { port:PORT,user:USER,host:HOST,storageLocation:TARGETLOCATION,storedFileName:TESTFILENAME}}`
    `#{CMD % { port:PORT,user:USER,host:HOST,storageLocation:TARGETLOCATION,storedFileName:METADATAFILENAME}}`
  end
  
  it 'should store a file and return true on success' do
    expect(@sc.transfer(TESTDIR+TESTFILENAME,TESTFILENAME)).to be true
  end
  
  it 'should return false if cannot store file' do
    sc=NoCloud::Connectors::RsyncSshStorageConnector.new(USER,HOST,NOTEXISTENTPORT) # wrong port
    sc.setStorageLocation TARGETLOCATION
    expect(sc.transfer(TESTDIR+TESTFILENAME,TESTFILENAME)).to be false
  end
  
  it 'should be able to fetch file if stored beforehand' do
    @sc.transfer TESTDIR+TESTFILENAME, TESTFILENAME
    @sc.fetch TESTFILENAME,TESTDIR+RETRIEVEDFILENAME 
    expect(File.exist? TESTDIR+RETRIEVEDFILENAME).to be true
  end
  
  it 'should raise an error if file is not there' do
    expect{@sc.fetch(NOTEXISTINGFILENAME,TESTDIR+RETRIEVEDFILENAME)}.to raise_error StandardError
    expect(File.exist? TESTDIR+RETRIEVEDFILENAME).to be false
  end
  
  it 'should detect if file exists' do
    @sc.transfer TESTDIR+TESTFILENAME, TESTFILENAME
    expect(@sc.exist? TESTFILENAME).to be true
  end
  
  it 'should detect if file not exists' do
    expect(@sc.exist? TESTFILENAME).to be false
  end
  
  it 'should return the file metadata' do 
    expect(@sc.getFileMetadata NOTEXISTINGFILENAME).to eq ''
    expect(@sc.getFileMetadata TESTFILENAME).to eq ''
    @sc.transfer TESTDIR+METADATAFILENAME, METADATAFILENAME
    expect(@sc.getFileMetadata TESTFILENAME).to eq 'this is the metadata record for file abc.txt'
  end
end
