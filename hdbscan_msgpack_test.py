import hdbscan
import os
import msgpack
import numpy as np
import struct


file = "/users/davidwiner/Documents/kai/tests/qa_tests/clustertest/mini_test.txt.model"

def format_float(num):
	return np.format_float_positional(num, trim='-')


if os.path.exists(file):
	with open(file, "rb") as app_embed_f:
		unpacker = msgpack.Unpacker(app_embed_f)
		
		data = []
		for i, unp in enumerate(unpacker):
			vect = []
			for i in range(0, len(unp), 4):
				f = struct.unpack('>f', unp[ i: i+4])
				vect.append(f[0])
			
			data.append(vect)
			
	
	clusterer = hdbscan.HDBSCAN(min_cluster_size=10)
	cluster_labels = clusterer.fit_predict(data, data)
	
	for l in cluster_labels:
		print(l)