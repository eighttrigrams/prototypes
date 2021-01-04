require_relative '../../spec_helper'


# author: Daniel M. de Oliveira
#
describe NoCloud::DataManagement::MetadataRecordManager do
  
  
  

  before :each do
    createTestDirs
    @m=NoCloud::DataManagement::MetadataRecordManager.new(DBLOCATION)
  end
  
  after :each do
    removeTestDirs
  end
  
  it 'should not initialize with nil argument' do
    expect{NoCloud::DataManagement::MetadataRecordManager.new(nil)}.to raise_error RuntimeError
  end
  
  it 'should not initialize with missing dbRootDir' do
    expect{NoCloud::DataManagement::MetadataRecordManager.new('/tmp/notexistent/notexistent')}.to raise_error RuntimeError
  end
  
  it 'should not initialize with a file as dbRootDir' do
    expect{NoCloud::DataManagement::MetadataRecordManager.new(TESTDIR+'.gitignore')}.to raise_error RuntimeError
  end


  
  it 'should rejectNotWellFormattedEntries' do
    expect(@m.incorporateEntry('2015-02-12 17:41:41 +0100,remoteHost2,' +TESTFILENAME+',md5,dabf',TESTFILENAME)).to be true
    expect(@m.incorporateEntry('2015-02-12 17:41:41 +010,remoteHost2,' +TESTFILENAME+',md5,dabf',TESTFILENAME)).to be false
    expect(@m.incorporateEntry('2015-02-12 17:41:41 +0100,remoteHost2,' +TESTFILENAME+',md5 dabf',TESTFILENAME)).to be false
  end
  
  it 'should create the file with incorporateEntry' do
    @m.incorporateEntry('2015-02-12 17:41:41 +0100,remoteHost2,'+TESTFILENAME+',md5,dabf',TESTFILENAME)
    expect(File.exist? DBLOCATION+RECORDFILENAME).to be true
  end
  
  it 'should incorporate an entry and add it' do
    @m.incorporateEntry('2015-02-12 17:41:41 +0100,remoteHost1,'+TESTFILENAME+',md5,dabf',TESTFILENAME)
    @m.incorporateEntry('2015-02-12 17:41:41 +0100,remoteHost2,'+TESTFILENAME+',md5,dabf',TESTFILENAME)
    
    content,lineCount=readFile(File.new DBLOCATION+RECORDFILENAME)
    expect(lineCount).to eq 2
    expect(content[0].include? ',remoteHost1,'+TESTFILENAME+',md5,dabf').to be true
    expect(content[1].include? '2015-02-12 17:41:41 +0100,remoteHost2,'+TESTFILENAME+',md5,dabf').to be true
  end
  
  it 'should ingest an entry and update an existing one' do
    @m.incorporateEntry('2015-02-12 17:41:41 +0100,remoteHost1,'+TESTFILENAME+',md5,dabf',TESTFILENAME)
    @m.incorporateEntry('2015-02-12 17:41:41 +0100,remoteHost1,'+TESTFILENAME+',md5,dabf',TESTFILENAME)
    
    content,lineCount=readFile(File.new DBLOCATION+RECORDFILENAME)
    expect(lineCount).to eq 1
    expect(content[0].include? '2015-02-12 17:41:41 +0100,remoteHost1,'+TESTFILENAME+',md5,dabf').to be true
  end
  
  it 'can work with hostnames consisting of deliberate characters' do
     expect(@m.incorporateEntry('2015-02-12 17:41:41 +0100,remote-Host-1,' +TESTFILENAME+',md5,dabf',TESTFILENAME)).to be true
  end
  
  it 'should be able to check if there is checksum for host' do
    expect(@m.hasUpToDateChecksumForHost? 'remoteHost1',TESTFILENAME, 100).to be false
    @m.incorporateEntry(Time.now.to_s+',remoteHost1,' +TESTFILENAME+',md5,dabf',TESTFILENAME)
    expect(@m.hasUpToDateChecksumForHost? 'remoteHost1',TESTFILENAME, 100).to be true
    expect(@m.hasUpToDateChecksumForHost? 'remoteHost2',TESTFILENAME, 100).to be false
  end
  
  it 'should detect if checksum for host is expired' do
     @m.incorporateEntry(Time.now.to_s+',remoteHost1,' +TESTFILENAME+',md5,dabf',TESTFILENAME)
     sleep 1
     expect(@m.hasUpToDateChecksumForHost? 'remoteHost1',TESTFILENAME, 100).to be true
     expect(@m.hasUpToDateChecksumForHost? 'remoteHost1',TESTFILENAME, 1).to be false
  end
  
  it 'should get checksum for host if exists' do
    @m.incorporateEntry(Time.now.to_s+',remoteHost1,' +TESTFILENAME+',md5,dabf',TESTFILENAME)
    expect(@m.getChecksum('remoteHost1',TESTFILENAME)).to eq 'dabf'
  end
  
  it 'should raise error if checksum not exists' do    
    expect{@m.getChecksum('remoteHost1',TESTFILENAME)}.to raise_error RuntimeError
    expect{@m.getChecksum('remoteHost1',TESTFILENAME)}.to raise_error RuntimeError
  end

  it 'should return the name of the file with the metadata record with the oldest modified date' do
    @m.incorporateEntry(Time.now.to_s+',remoteHost1,'+'oldest.'+TESTFILENAME+',md5,dabf','oldest.'+TESTFILENAME)
    @m.incorporateEntry(Time.now.to_s+',remoteHost1,'+TESTFILENAME+',md5,dabf',TESTFILENAME)
    expect(@m.file_name_oldest_record).to eq 'oldest.'+TESTFILENAME
  end
  
  it 'should return nothing if no records' do
    expect(@m.file_name_oldest_record).to be nil
  end

  it 'should add and return checksums' do
    @m.set_orig_chksum TESTFILENAME,'dddg'
    @m.set_orig_chksum TESTFILENAME+"2",'aaa'
    expect(@m.get_orig_chksum TESTFILENAME).to eq 'dddg'
    expect(@m.get_orig_chksum TESTFILENAME+"2").to eq 'aaa'
    expect(@m.get_orig_chksum TESTFILENAME+"3").to eq nil
  end



end