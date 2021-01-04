require_relative '../lib/no_cloud'
require 'rspec'


TESTDIR = 'resources/spec/common/'
STORAGELOCATION=TESTDIR+'storage/'
OWNEDLOCATION = STORAGELOCATION+'devHost/'
DBLOCATION = TESTDIR+'db/'
REMOTEHOST1LOCATION = STORAGELOCATION+'remoteHost1/'
REMOTEHOST2LOCATION = STORAGELOCATION+'remoteHost2/'
TESTFILENAME = 'abc.txt'
FETCHEDFILENAME='abc.txt.retrieved'
NOTEXISTINGFILENAME=TESTFILENAME+'.notexisting'
METADATAFILENAME = TESTFILENAME+'.record'
NOTEXISTINGDIR = '/NOTEXISTING/'
RECORDFILENAME=TESTFILENAME+'.record'
DEFAULTCHECKSUMTIMEOUT=72000


def createTestDirs
  FileUtils.mkdir(STORAGELOCATION)
  FileUtils.mkdir(OWNEDLOCATION)
  FileUtils.mkdir(REMOTEHOST1LOCATION)
  FileUtils.mkdir(REMOTEHOST2LOCATION)
  FileUtils.mkdir(DBLOCATION)
end

def removeTestDirs
  FileUtils.rmtree(STORAGELOCATION)
  FileUtils.rmtree(DBLOCATION)
end 


def writeFile(fileName,content)
  file=File.new fileName, 'w'
  file.puts content
  file.close
end

# returns an array of lines and the line count
#
def readFile(fileName)
  file=File.new fileName, 'r'
  content=[]
  lineCount=0
  file.each {|line|
    content << line
    lineCount=lineCount+1
  }
  file.close
  return content,lineCount
end


def stripTrailingSlash(path)
  return path.gsub(/\/$/,'')
end

