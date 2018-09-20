from lexicalchain import LexGraph
import nltk
from nltk.corpus import conll2000
from nltk.tag import pos_tag
from nltk.tokenize import word_tokenize, sent_tokenize
from nltk.corpus import wordnet as wn
import logging
import os
import heapq
'''for chunking'''
train_sents = conll2000.chunked_sents('train.txt', chunk_types=['NP'])

class ChunkParser(nltk.ChunkParserI):
	def __init__(self, train_sents):
		train_data= [[(t,c) for w,t,c in nltk.chunk.tree2conlltags(sent)] for sent in train_sents]
		self.tagger = nltk.TrigramTagger(train_data)
	def parse(self, sentence):
		pos_tags= [pos for (word,pos) in sentence]
		tagged_pos_tags = self.tagger.tag(pos_tags)
		chunktags= [chunktag for (pos, chunktag) in tagged_pos_tags]
		conlltags= [(word, pos, chunktag) for ((word,pos),chunktag) in zip(sentence, chunktags)]
		return nltk.chunk.conlltags2tree(conlltags)
NPChunker= ChunkParser(train_sents)

os.chdir('data')
for file_name in os.listdir():
	'''text input,cleaning,pos-tagging, and NP chunking'''
	with open(file_name, 'r',encoding = 'utf-8') as content_file:
		clean_input = content_file.read()
		for k in '!"#$%&*+/:;<=>?@\^_`|~()[]{}=':
			clean_input = clean_input.replace(k,'')
		clean_input = clean_input.replace("-\n","")
		passages = clean_input.split(".\n")
		paras = []
		for passage in passages:
			passage = passage.replace("\n","")
			paras.append(sent_tokenize(passage))

		final_input = []
		for para in paras:
			final_input.append([pos_tag(word_tokenize(sent)) for sent in para])

	'''Lexical chain generation'''
	mc = LexGraph(final_input)
	mc.reduce_graph()
	outfile = open('../file.txt','w',encoding = 'utf-8')
	outfile.write('chains\n')
	for sense,chain in mc.metachains.items():
		if chain.score>1:
			if type(sense) == int:
				synset = wn._synset_from_pos_and_offset('n',sense)
			else:
				synset = sense

	"Noun Phrase genertation"
	sentences = []
	for para in final_input:
		sentences = sentences + para
	NP_set = set()
	NP_List = []
	for sent in sentences:
		sent = nltk.chunk.tree2conlltags(NPChunker.parse(sent))
		noun_phrase = ""
		for triplet in sent:
			if triplet[2] == "B-NP":
				if noun_phrase != "":
					NP_List.append(noun_phrase.lower())
					NP_set.add(noun_phrase.lower())
				noun_phrase = triplet[0]
			elif triplet[2] == "I-NP":
				noun_phrase = noun_phrase + " " + triplet[0]
			elif noun_phrase != "":
				NP_List.append(noun_phrase.lower())
				NP_set.add(noun_phrase.lower())
				noun_phrase = ""
	with open('../NounPhrases/'+file_name,'w',encoding = 'utf-8') as noun_phrase_file:
		for phrase in NP_set:
			noun_phrase_file.write(phrase)
			noun_phrase_file.write('\n')

	"""Scoring of noun phrases"""
	phrase_score = {}
	total_length = mc.current_word_index + 1
	for phrase in NP_set:
		score = 0
		phrase_range = [0,total_length - 1]
		words = phrase.split(' ')
		for word in words:
			if word in mc.wsdict:
				sense = mc.wsdict[word][0]
				score += mc.metachains[sense].score
				word_range = mc.word_range[word]
				if phrase_range[0]<word_range[0]:
					phrase_range[0]= word_range[0]
				if phrase_range[1]>word_range[1]:
					phrase_range[1]= word_range[1]
		score = score*(phrase_range[1]-phrase_range[0]+1)/total_length
		phrase_score[phrase] = score

	if len(phrase_score) < 10:
		final_phrases = heapq.nlargest(len(phrase_score), phrase_score, key=phrase_score.get)
	else:
		final_phrases = heapq.nlargest(10, phrase_score, key=phrase_score.get)
	with open('../FinalPhrases/'+file_name,'w',encoding = 'utf-8') as final_phrase_file:
		for phrase in final_phrases:
			final_phrase_file.write(phrase)
			final_phrase_file.write('\n')
