require_relative '../../spec_helper'


# author: Daniel M. de Oliveira
#
describe NoCloud::DataManagement::DatastoreRecordManager do
  
  before :all do
    createTestDirs
  end
  
  after :all do
    removeTestDirs
  end
  
  before :each do
    @m=NoCloud::DataManagement::DatastoreRecordManager.new 'local',STORAGELOCATION
  end
  
  it 'should check if custody location exist' do
    expect{NoCloud::DataManagement::DatastoreRecordManager.new 'local',NOTEXISTINGDIR}.to raise_error RuntimeError
  end
  
  it 'should add entries' do
    @m.add_checksum('remoteHost1',TESTFILENAME,'md5','dabf')
    @m.add_checksum('remoteHost2',TESTFILENAME,'md5','dddd')
    content,lineCount=readFile(REMOTEHOST1LOCATION+RECORDFILENAME)
    expect(lineCount).to eq 1
    expect(content[0].include? ',local,'+TESTFILENAME+',md5,dabf').to be true
    content,lineCount=readFile(REMOTEHOST2LOCATION+RECORDFILENAME)
    expect(lineCount).to eq 1
    expect(content[0].include? ',local,'+TESTFILENAME+',md5,dddd').to be true
  end
  
  it 'should update an entry' do
    @m.add_checksum('remoteHost1',TESTFILENAME,'md5','dabf')
    @m.add_checksum('remoteHost1',TESTFILENAME,'md5','dddd')
    content,lineCount=readFile(REMOTEHOST1LOCATION+RECORDFILENAME)
    expect(lineCount).to eq 1
    expect(content[0].include? ',local,'+TESTFILENAME+',md5,dddd').to be true
  end
  
  it 'should raise error if dir not exist' do
    expect{@m.add_checksum('remoteHost3',TESTFILENAME,'md5','dddd')}.to raise_error RuntimeError
  end

  it 'should return a record' do
    @m.add_checksum('remoteHost1',TESTFILENAME,'md5','dabf')
    expect(@m.get_record('remoteHost1',TESTFILENAME).include? ",").to be true
  end
  
end