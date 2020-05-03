srcdir= src
docsdir= docs
makedirs = $(srcdir) $(docsdir)

all:	
	@for subdir in $(makedirs); do \
		(cd $$subdir && $(MAKE) all) || exit 1; \
	done
	$(MAKE) javadoc

clean:	
	@for subdir in $(makedirs); do \
		(cd $$subdir && $(MAKE) clean) || exit 1; \
	done

javadoc:
	cd $(srcdir) && $(MAKE) javadoc

count:
	cd $(srcdir) && $(MAKE) count

tar backup:
	cd .. && qmorph/scripts/mkbackup qmorph

