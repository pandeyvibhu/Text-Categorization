from collections import defaultdict
from nltk.corpus import wordnet as wn
import os
# outfile1 = open('../lexnode.disambig.txt','w',encoding = 'utf-8')
class LexNode:
	
	""" Representation of a word token, its position in the document and its (supposed) sense, and score(based on edges weigth sum leaving the node)"""
	def __init__(self,word,sensenum,wordindex,spos,ppos):
		self.word = word
		self.sensenum = sensenum
		self.wordindex = wordindex
		self.score = -1      
		self.id = self.sensenum if self.sensenum else self.word

		self.spos = spos
		self.ppos = ppos
		self.connected_chains = {}

	def _link_to_chain(self,m_chain,link_node_chain):
		"Links with a chain"
		Metachain._type_check(m_chain)
		Link_Node_Chain._type_check(link_node_chain)
		self.connected_chains[m_chain] = link_node_chain

	def pos(self):
		""" @return: Tuple of sentence and paragraph number of this node """
		return self.spos, self.ppos
    
	def dist(self, other):
		"""
		@param other: LexNode to which the distance is computed  
		@return: Tuple of distances between sentence and paragraph positions of this and another node. """
		LexNode._type_check(other)
		return abs(self.spos - other.spos), abs(self.ppos - other.spos)

	def _print(self):
		return ("word: " +  str(self.word) + ", sense_num: " + str(self.sensenum) + ", word_index: " + str(self.wordindex) + ", sentence_position: " + str(self.spos) + ", paragraph_position: " + str(self.ppos))
	
	@staticmethod
	def _type_check(obj):
		if not isinstance(obj,LexNode): raise TypeError

class Link_Node_Chain:
	"""Link LexNode with metachain"""
	class Type:
		""" Simple enum-like class """
		count = 6
		IDENT, SYN, HYPER, HYPO, SIBLING, OTHER  = range(count)
		@classmethod
		def validate(cls, val):  
		    if not 0 <= val < cls.count:   raise TypeError(str(val)+" is not a valid Link_Node_Chain.Type")

	def __init__(self, type=Type.OTHER):
		'''
		@param lex_dist: the lexical distance between the nodes (usually WordNet tree distance)
		@param type: Type of the relation
		'''
		Link_Node_Chain.Type.validate(type)

		self.type = type

	@staticmethod
	def _type_check(obj):
		if not isinstance(obj,Link_Node_Chain): raise TypeError

class Metachain:
	def __init__(self,sense):
		self.head_node = None
		self.sense = sense
		self.node_list = []
		self.node_to_link ={}
		self.score = 0

	def _link_to_node(self,l_node,link_node_chain):
		"Links itself with a node"
		LexNode._type_check(l_node)
		Link_Node_Chain._type_check(link_node_chain)
		if not self.head_node and (link_node_chain.type == Link_Node_Chain.Type.IDENT or link_node_chain.type == Link_Node_Chain.Type.SYN) :
			assert l_node.id == self.sense
			self.head_node = l_node
		self.node_list.append(l_node) 
		self.node_to_link[l_node] = link_node_chain

	@staticmethod
	def _type_check(obj):
		if not isinstance(obj,Metachain): raise TypeError

class LexGraph:
	def __init__(self,POS_paras):
		self.word_to_lexnodes = {}
		self.current_para_pos = 0
		self.current_word_index = 0
		self.current_sent_pos = 0
		self.metachains = {}
		self.wsdict = {}
		self.word_range = {}
		self.feed_document(POS_paras)
	def chains(self):
		return metachains

	def is_noun(self,POS_word):
		assert isinstance(POS_word,tuple)
		assert len(POS_word) == 2
		if POS_word[1][0] == 'N':
			return True
		else:
			return False

	def feed_document(self,POS_paras):
		"POS tagged paragraph list as input"
		for POS_para in POS_paras:
			self.current_para_pos += 1

			for POS_sent in POS_para:
				self.current_sent_pos += 1

				for POS_word in POS_sent:
					self.current_word_index += 1
					if self.is_noun(POS_word):
						self.add_word(POS_word)

	

	def node_link_chain(self,word_or_sensenum,l_node,link_type):

		"word_or_sensenum of the metachain to which l_node must be linked to"
		if word_or_sensenum not in self.metachains:	
			chain = Metachain(word_or_sensenum)
			self.metachains[word_or_sensenum] = chain
		else:
			chain = self.metachains[word_or_sensenum]

		link = Link_Node_Chain(link_type)
		chain._link_to_node(l_node,link)
		l_node._link_to_chain(chain,link)

	def add_node_to_graph(self,word,sensenum):
		
		l_node = LexNode(word,sensenum,self.current_word_index,self.current_sent_pos,self.current_para_pos)
		if word in self.word_to_lexnodes:
			self.word_to_lexnodes[word].append(l_node)
		else:
			self.word_to_lexnodes[word] = [l_node]

		return l_node

	def add_word(self,POS_word):
		"Adds word to the graph if it is a noun"
		word = POS_word[0].lower()
		syns = wn.synsets(word,'n')
		if not syns:
			l_node = self.add_node_to_graph(word,None)
			self.node_link_chain(word,l_node,Link_Node_Chain.Type.IDENT)
		else:
			for syn in syns:
				assert '.n.' in str(syn)
					
				hypers = syn.hypernyms()
				hypos = syn.hyponyms()

				l_node = self.add_node_to_graph(word,syn.offset())
				self.node_link_chain(syn.offset(),l_node,Link_Node_Chain.Type.SYN)
				syns_linked_to = [syn]

				for hyper in hypers:
					if hyper not in syns_linked_to:
						self.node_link_chain(hyper.offset(),l_node,Link_Node_Chain.Type.HYPER)
						syns_linked_to.append(hyper)
					for sibl in hyper.hyponyms():
						if sibl not in syns_linked_to:
							self.node_link_chain(sibl.offset(),l_node,Link_Node_Chain.Type.SIBLING)
							syns_linked_to.append(sibl)

				for hypo in hypos:
					if hypo not in syns_linked_to:
						self.node_link_chain(hypo.offset(),l_node,Link_Node_Chain.Type.HYPO)
						syns_linked_to.append(hypo)
	
	def _score_node(self, lexnode):
		""" Computes the score of a single LexNode"""
		score = 0
		
		"For each meta chain this node is in:"
		chain_link = lexnode.connected_chains
		for chain, link in chain_link.items():
			"For each LN in that chain"
			if chain.head_node != None:
				for otherln, otherlink in chain.node_to_link.items():
					"Do not score nodes belonging to the same word token!"
					if otherln.wordindex == lexnode.wordindex:    continue
					score += self._score_lnk(lexnode, link, otherln, otherlink)
		lexnode.score = score
		# outfile1.write(lexnode.word + " " + str(lexnode.sensenum) + " " +str(lexnode.wordindex)+" "+str(score) + '\n')

		return score
    
	def _score_lnk(self, ln, link, lnother, linkother):
		sdist, pdist = ln.dist(lnother)
		return self._score_from_matrix(self._rel_between_nodes(ln, lnother), sdist, pdist)
        
	def _score_from_matrix(self, rel, sd, pd):
		'''  Implementation of scoring matrix given by Galley & McKeown 2003
		@param rel: relation type
		@param sd: sentence dist
		@param pd: paragraph dist 
		@return: float, score
		'''
		if rel == Link_Node_Chain.Type.IDENT or rel == Link_Node_Chain.Type.SYN:
			if sd <= 3 and sd>=-3: return 1
			return .5
		if rel == Link_Node_Chain.Type.HYPER or rel == Link_Node_Chain.Type.HYPO:
			if sd <= 1 and sd>=-1: return 1
			if sd <= 3 and sd>=-3: return .5
			return .3
		if rel == Link_Node_Chain.Type.SIBLING:
			if sd <= 1 and sd>=-1: return 1
			if sd <= 3 and sd>=-3: return .3
			if pd <= 1 and pd>=-1: return .2
			return 0
		return 0

	def _rel_between_nodes(self, ln1, ln2):
		"relation of ln1 w.r.t ln2"
		if ln1.word == ln2.word:    return Link_Node_Chain.Type.IDENT
		if ln1.sensenum == ln2.sensenum:    return Link_Node_Chain.Type.SYN
		assert ln1.id in self.metachains and ln2.id in self.metachains
		ln1Chain = self.metachains[ln1.id]
		"what is ln2's word sense chain relation with ln1's sense"
		if ln2 in ln1Chain.node_to_link:
			return ln1Chain.node_to_link[ln2].type
		else:
			" the case where ln1 is hyper of the metachain and ln2 is hypo in the metachain or vice versa"
			return None

	def disambig_word(self,word):
		sense_score = {}
		for lexnode in self.word_to_lexnodes[word]:
			assert lexnode.word == word
			self._score_node(lexnode)
			if lexnode.sensenum not in sense_score:
				sense_score[lexnode.id] = lexnode.score
			else:
				sense_score[lexnode.id] += lexnode.score
		max_sense = max(sense_score, key=sense_score.get)
		self.wsdict[word] = [max_sense,sense_score[max_sense]]
	
	def reduce_graph(self):
		for word in self.word_to_lexnodes:
			self.disambig_word(word)
		for lexchain in list(self.metachains.values()):
			self.reduce_chain(lexchain)
			self.score_chain(lexchain)

		"Calculates individual scores of each lexnode(after removal of WRONG sense) and total score of each (correct)lexnode corresponding to a word"
		for word in self.word_to_lexnodes:
			word_score = 0
			sense = self.wsdict[word][0]
			
			for lexnode in self.word_to_lexnodes[word]:
				if lexnode.id == sense:
					self._score_node(lexnode)
					assert lexnode.score != -1
					word_score += lexnode.score
			
			self.wsdict[word] = [sense,word_score]

		for word in self.wsdict:
			self.calc_word_range(word)

	def calc_word_range(self,word):
		"Calculates the range of the chain which represents the word's sense"
		chain = self.metachains[self.wsdict[word][0]]
		assert len(chain.node_list)>0
		self.word_range[word] = [chain.node_list[0].wordindex,chain.node_list[-1].wordindex]

	def reduce_chain(self,lexchain):
		for lexnode in lexchain.node_list:
			word = lexnode.word
			word_or_sensenum = lexnode.id
			maxsense = self.wsdict[word][0]
			if word_or_sensenum != maxsense:
				del lexchain.node_to_link[lexnode]
				lexchain.node_list.remove(lexnode)

	def score_chain(self,lexchain):
		lexchain.head_node = None

		nodes = lexchain.node_list
		if len(nodes) <= 1:
			return 0

		chain_score = 1
		link_dict = lexchain.node_to_link
		if link_dict[nodes[0]].type == Link_Node_Chain.Type.IDENT or link_dict[nodes[0]].type == Link_Node_Chain.Type.SYN:
			lexchain.head_node = nodes[0]
		for ind in range(len(nodes)-1):
			node1 = nodes[ind]	
			node2 = nodes[ind+1]
			incr = self._score_lnk(node1,link_dict[node1],node2,link_dict[node2])
			if link_dict[node2].type == Link_Node_Chain.Type.IDENT or link_dict[node2].type == Link_Node_Chain.Type.SYN:
				if incr == 0:
					incr = 0.5
				if lexchain.head_node == None:
					lexchain.head_node = node2
			chain_score += incr

		lexchain.score = chain_score
		if lexchain.head_node == None:
			lexchain.score = 0

