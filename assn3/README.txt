The Lucene Directory contains my best working attempt at getting Lucene functioning on my Windows 10 computer.

IndexFiles.java and SearchFiles.java were the default java files provided to us at the beginning of the semester when he insisted we set up lucene. In order to run IndexFiles through eclipse, one need to go to run and change the args to be "-docs gutenberg-corpus\"

THESE FILES ARE NOT PERTINENT TO THE ASSIGNMENT, AND FUNCTION ONLY AS A FIRST TEST OF LUCENE FUNCTIONALITY ON YOUR SYSTEM.

DocumentIndexing.java, ExplainScoring.java, and BasicSearch.java are the sample files provided by Dr. Gudivada.

PROCESS:

Based on the instructions from Piazza, the first thing I am doing is taking the original cs-bibliography.bib file and running it through a cleaner, then a parser in a python terminal.

The first part of this is cloning the repositories for CleanBib (credit to ZacCat https://github.com/ZacCat/clean_bib.git) and BibtexParser (credit to sciunto-org, https://github.com/sciunto-org/python-bibtexparser).

Following the readme for CleanBib, I installed BibtexParser in my python environment, set the unwanted fields to:
unwanted = ['interhash', 'intrahash', 'timestamp', 'biburl', 'added-at', 'urldate']
and ran Bib_Clean.py using 

python bib_clean.py -i cs-bibliography.bib -o "cs-clean.bib"

Actually completing this cleaning successfully requires changing the open encodings in bibclean to utf-8 and changing the parser as I described in piazza: https://piazza.com/class/jq0zi646hoz5qb?cid=160