#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import time

from selenium.common.exceptions import StaleElementReferenceException

STALE_ELEMENT_RETRY_COUNT = 5
STALE_ELEMENT_SLEEP_TIME = 0.1


def add_stale_protection(func):
	def decorated_func(*args, **kwargs):
		for i in range(0, STALE_ELEMENT_RETRY_COUNT):
			try:
				return func(*args, **kwargs)
			except StaleElementReferenceException:
				time.sleep(STALE_ELEMENT_SLEEP_TIME)
				pass

		raise Exception('Stale Element Fetching failed')

	return decorated_func
