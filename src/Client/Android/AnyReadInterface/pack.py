#
#@desc: packet android interface into jar package
#
#@author: Leon
#

import os, sys

li = ['ReaderHandler', 'ReaderActivity', 'R$', 'R.']


def do_pack(path, pack_name, index):
	directory = pack_name.replace(".", "/");
	txt = '';
	fullname = os.path.join(path, directory)
	print fullname
	for file in os.listdir(fullname):
		flag = True
		for prefix in li:
			if file.startswith(prefix):
				flag = False
				break
		if flag and file.endswith('.class'):
			txt += os.path.join(directory, file) + ' '

	command = "jar cvf anyRead_%s.jar %s" % (index, txt);
	print "============== Ready to Run ================="
	print command
	os.system(command)
	print "============== End to Run ================="



if __name__ == '__main__':
	if len(sys.argv) == 2:
		do_pack("bin/classes", "com.sohu.wuhan", sys.argv[1])
	elif len(sys.argv) != 4:
		print "Usage: pack.py path package_name index"
		sys.exit();
	else:
		do_pack(sys.argv[1], sys.argv[2], sys.argv[3])



