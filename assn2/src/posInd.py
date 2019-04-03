
import os
import sys
##import nltk

if len(sys.argv) < 2:
	sys.exit("Incorrect command line arguments. Must run the program with the following commandline format: \n\n" + 
		r'     positionalIndex.py "path"' + "\n\n" + r'Where "path" is a string that contains the path to the folder that contains the ' +
		"corpus that will be read by this program. ")
		
corLocation = ' '.join(sys.argv[1:])
filenames = os.listdir(corLocation)


def dataIntoWords(data):
	data = data.lower() #edit the input to only include words. hyphenated words will be separated. apostrophes will be deleted. Example: "Jason's" will become "jasons" 
	charToReplace = "\n\t;:,./\\-"
	for char in charToReplace:
		data = data.replace(char, ' ')
	
	charToRemove = "!?\'\""
	for char in charToRemove:
		data = data.replace(char, '')
	return data.split()

def printPositionalArray(posInd): #dic to be printed on std output and n is the "n"gram. So 1 for unigram, 2 for bigram...
		
	print( '{:<25s} {}'.format("Token", "Document and Positions") )
	for word in posInd:
		
		print('{:<25s}'.format(word))
		
		for file in posInd[word]:
			print('{:<25s}'.format(''), '{:<10s}'.format(file), posInd[word][file],'\n')

		print()
posInd = {}

	# make a dictionary with a dictionary in it. the dict( key, dict2). dict2 key will be the file number and the key will be the list in there.
for x in filenames: #for each file
	with open(corLocation+ "\\" + x, "r") as openedFile:#opens the file and closes it when its done using it.
		data = openedFile.read() #copy the data
		## normalize
		##words = nltk.word_tokenize(data)	
		words = dataIntoWords(data)
			
		for index in range(len(words)):
			word = words[index]
			
			posInd.setdefault(word, {})
			posInd[word].setdefault(x, [])
			posInd[word][x].append(index)
			
printPositionalArray(posInd)
		 
		
		
