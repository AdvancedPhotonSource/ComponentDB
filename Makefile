# The top level makefile. Targets like "all" and "clean"
# are defined in the RULES file.

TOP = .
#SUBDIRS = irmis src
SUBDIRS = src

include $(TOP)/tools/make/RULES_CMS

