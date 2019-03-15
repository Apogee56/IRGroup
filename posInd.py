import os
import sys
import datetime
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

def printResultArray(result): #Prints the result structure in a similar fashion to the 
		
	print( '{:<25s} {}'.format("Results") )
	for file in result:
		
		print('{:<25s}'.format(file))
		
		print(result[file] + "\n")

def proxIntersect(p1,p2,k): #implements the proximity query algorithm provided in figure 2.12, page 39 of our book.
        #The function returns a dictionary with documents as keys and positional pairs as values.
        #The function takes a term p1, a term p2, and a distance k that must be satisfied.
		#This implementation is essentially faithful to the psuedocode except that it returns the output in a slightly different format.
		#Exits with empty result if a query term is not in the index
		if posInd.get(p1) == None or posInd.get(p2) == None:
			print("No results found")
			return result
		len_p1 = len(posInd[p1])
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
							if abs(pos1 - pos2) <= k:
								l.append(pos2)
							elif pos2 > pos1:
								break
						while ((len(l) != 0) and (abs(l[0] - pos1) > k)):
							l.pop(0)
						result.setdefault(doc, [])
						for position2 in l:
							result[doc].append((pos1,position2))
		return result

def phraseQuery(str): #takes a set of query terms in the form of a space separated string of words, and then returns the first pair of adjacent terms that in that phrase phrase.\
		#This method functions by working backwards through the phrase in order to end with the first word of the query.
	
	terms = str.split(" ")
	result = {}
	res_temp = {}
	#aborts the search and returns empty result if a phrase with a non-existant term is found. 
	for i in terms:
		if posInd.get(i) == None:
			print("No results found")
			return result
	if(len(terms) <= 1):
		result = posInd[terms[0]]
	elif(len(terms) == 2):
		res_temp = proxIntersect(terms[0],terms[1],1)
		#This removes results of the proximity query for which the phrase is in the wrong order.
		for i in res_temp:
			for x in res_temp[i]:
				a, b = x
				if (a < b):
					#print("added")
					result.setdefault(i,[])
					result[i].append(x)
	else:
		terms.reverse()
		res_temp_0 = proxIntersect(terms[0],terms[1],1)
		terms.remove(terms[0])
		while len(terms) > 1:
			res_temp = proxIntersect(terms[0],terms[1],1)
			res_temp_2 = {}
			#Takes the intersection of the two results.
			for i in res_temp_0:
				for j in res_temp:
					#If the documents contained are the same:
					if(i == j):
						for x in res_temp_0[i]:
							for y in res_temp[j]:
								#unpacking the tuples in i and j:
									a, b = x
									c, d = y
									if(a > b and c > d and b == c):
										#setting the default value of the document if it has no tuples yet
										res_temp_2.setdefault(i, [])
										#At this point, we have shown that these three terms are next to each other in the proper order in the same document, and we thus append them to the result.
										res_temp_2[i].append(y)
			#Once out of this loop, all documents with all tuples have been compared, and thus, we replace our old end result with our new one.
			res_temp_0 = res_temp_2
			terms.remove(terms[0]);
		#Once out of this loop, we lack enough terms to continue performing a comparison. That is, we have finished running the query.			
		#Before we are done in here, we must reverse the tuples so that the positions return in the appropriate order.
		for i in res_temp_0:
			for a,b in res_temp_0[i]:
				result.setdefault(i,[])
				result[i].append((b,a))
	return result			
			
		
		
		
		
posInd = {}




	# make a dictionary with a dictionary in it. the dict( key, dict2). dict2 key will be the file number and the key will be the list in there.
for x in filenames: #for each file
	#switchOS '/' is used for mac  (or) '\\' is used for windows 
	with open(corLocation+ "/" + x, "r", encoding="utf8") as openedFile:#opens the file and closes it when its done using it.
		#used to take out hidden file on mac
		if(x == ".DS_Store"): continue
		data = openedFile.read() #copy the data
		## normalize
		##words = nltk.word_tokenize(data)	
		words = dataIntoWords(data)
			
		for index in range(len(words)):
			word = words[index]
			
			posInd.setdefault(word, {})
			posInd[word].setdefault(x, [])
			posInd[word][x].append(index)
			
		
#print(datetime.datetime.now())		
#printPositionalArray(posInd)
#res = proxIntersect('this', 'hope', 1)
#print(res)
#res = phraseQuery("hope")
#print(res)

#res = phraseQuery("hope this")
#print(res)

#res = phraseQuery("hope this was")
#print("hope this was")
#print(res)

#This checks that an invalid query doesn't crash the program.
#res = phraseQuery("hope this apple")
#print("hope this apple")
#print(res)

#used for retrieving only numbers from the input
def Search_number_String(String):
    index_list = []
    del index_list[:]
    for i, x in enumerate(String):
        if x.isdigit() == True:
            index_list.append(i)
    start = index_list[0]
    end = index_list[-1] + 1
    number = String[start:end]
    return number

#Query Parser Used to parse for either phrase or proximity query
def queryParser(phraseQ):
    if("quit" == phraseQ):
        answer = "Thanks for searching"
        return answer
    #Tests for distance variables in phrase
    elif('/' in phraseQ):
        phraseQ = phraseQ.split()
        str = phraseQ[1]
        phraseQ[1] = Search_number_String(phraseQ[1])
        print(phraseQ)
        answer = proxIntersect(phraseQ[0], phraseQ[2], int(phraseQ[1]))
        return answer
    else:
        answer = phraseQuery(phraseQ)
        print(answer)
        return answer

#Testing
CanSearch = True
while (CanSearch):
        phraseQ = input("Please enter a phrase / proximity query or type quit to exit: ")
        phraseQ = phraseQ.lower()
        ans = queryParser(phraseQ)
        print(ans)
        if("Thanks for searching" in ans):
            CanSearch = False
#testing 