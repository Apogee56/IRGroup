
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


def proxIntersect(p1,p2,k): #implements the proximity query algorithm provided in figure 2.12, page 39 of our book.
        #The function returns a dictionary with documents as keys and positional pairs as values.
        #The function takes a term p1, a term p2, and a distance k that must be satisfied.

        #This implementation is essentially faithful to the psuedocode except that it returns the output in a slightly different format.

        len_p1 = len(posInd[p2])
        len_p2 = len(posInd[p2])
        result = {}
        docs = []
        
        for i in posInd[p1].keys():
                for j in posInd[p2].keys():
                        if (i == j):
                                docs.append(i)

        #print(posInd[p1].keys())
        #print(docs)

        for doc in docs:
                l1 = posInd[p1][doc]
                l2 = posInd[p2][doc]
                l = []
                for pos1 in l1:
                        for pos2 in l2:
                                if abs(pos1-pos2) <= k:
                                        l.append(pos2)
                                elif pos2 > pos1:
                                        break
                        while ((len(l) != 0) and (l[0] - pos1 > k)):
                               l.pop(0)
                        result.setdefault(doc, [])
                        for position2 in l:
                                result[doc].append((pos1,position2))
        return result

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
#res = proxIntersect('this', 'hope', 1)
#print(res);

		
		
