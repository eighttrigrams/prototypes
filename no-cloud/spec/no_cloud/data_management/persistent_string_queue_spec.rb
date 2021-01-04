require_relative '../../spec_helper'


#
#
# author: Daniel M. de Oliveira
#
describe NoCloud::DataManagement::PersistentStringQueue do
  
  FS_SEPARATOR = '/'
  DBFILENAME='persistentstringqueue.csv'
  EMPTY=''
  
  before :each do
    createTestDirs
    @db=NoCloud::DataManagement::PersistentStringQueue.new DBLOCATION
  end
  
  after :each do
    FileUtils.rm stripTrailingSlash(DBLOCATION) if File.file? stripTrailingSlash(DBLOCATION) 
    removeTestDirs
  end
  
  it 'can create an instance' do
    db=NoCloud::DataManagement::PersistentStringQueue.new DBLOCATION
    expect(db.nil?).to be false
  end
  
  it 'should raise an error if dblocation not found' do
    expect{NoCloud::DataManagement::PersistentStringQueue.new NOTEXISTINGDIR}.to raise_error ArgumentError, NoCloud::Errors::ERR_MSG_FILE_NOT_FOUND+NOTEXISTINGDIR
  end
  
  it 'should raise an error if dblocation is a file' do
    FileUtils.rm_r DBLOCATION
    writeFile(stripTrailingSlash(DBLOCATION),EMPTY)
    
    expect{NoCloud::DataManagement::PersistentStringQueue.new DBLOCATION}.to raise_error ArgumentError, NoCloud::Errors::ERR_MSG_SHOULD_BE_DIRECTORY+stripTrailingSlash(DBLOCATION)
  end
  
  it 'should accept missing slash' do
    db=NoCloud::DataManagement::PersistentStringQueue.new stripTrailingSlash(DBLOCATION)
    expect(db.nil?).to be false
  end
  
 
 
  
  
  it 'should not overwrite when creating instance' do
    @db.enqueue 'abc'
    NoCloud::DataManagement::PersistentStringQueue.new DBLOCATION
    content,lineCount=readFile(DBLOCATION+DBFILENAME)
    expect(content[0].include? 'abc').to be true
  end
  
  it 'should add an entry' do
    @db.enqueue 'abc'
    content,lineCount=readFile(DBLOCATION+DBFILENAME)
    expect(content[0].include? 'abc').to be true
  end
  
  it 'should add another entry' do
    @db.enqueue 'abc'
    @db.enqueue 'cde'
    content,lineCount=readFile(DBLOCATION+DBFILENAME)
    expect(content[0].include? 'abc').to be true
    expect(content[1].include? 'cde').to be true
  end
  
  it 'should fetch and entry' do
    @db.enqueue 'abc'
    expect(@db.dequeue.include? 'abc').to be true
  end
  
  it 'should dequeue more entries' do
    @db.enqueue 'abc'
    @db.enqueue 'cde'
    expect(@db.dequeue).to eq 'abc' 
    expect(@db.dequeue).to eq 'cde'
  end
  
  it 'should return nil if nothing in queue' do
    expect(@db.dequeue.nil?).to be true
  end
  
  it 'should return nil if trying to remove more entries than put in' do
    @db.enqueue 'abc'
    @db.enqueue 'cde'
    @db.dequeue
    @db.dequeue
    expect(@db.dequeue.nil?).to be true
  end
  
  
  
end