#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.

WebDriverWait that explains timeouts: what was awaited plus the page state
(URL, faces messages, growls, loading dialog, browser console errors).
A screenshot and page source are saved to FAILURE_ARTIFACT_DIR.
"""
import os
import re
import time

from selenium.common.exceptions import TimeoutException, WebDriverException
from selenium.webdriver.common.by import By
from selenium.webdriver.support.wait import WebDriverWait

FAILURE_ARTIFACT_DIR = os.path.join(os.getcwd(), 'test-failures')

# Set by tests/conftest.py so all artifacts of one failing test share a file name prefix.
current_test_label = 'unknown-test'


def sanitize_file_label(label, max_length=80):
    return re.sub(r'[^A-Za-z0-9_.-]+', '_', label).strip('_')[:max_length]


def failure_artifact_base(suffix):
    """Path prefix <test>-<timestamp>-<suffix> inside FAILURE_ARTIFACT_DIR (directory is created)."""
    os.makedirs(FAILURE_ARTIFACT_DIR, exist_ok=True)
    name = '%s-%s-%s' % (sanitize_file_label(current_test_label), time.strftime('%Y%m%d-%H%M%S'),
                         sanitize_file_label(suffix))
    return os.path.join(FAILURE_ARTIFACT_DIR, name)

# Visible faces/PrimeFaces messages that usually explain a failed action.
MESSAGE_XPATH = ('//*[contains(@class,"ui-growl-message") or contains(@class,"ui-messages-error")'
                 ' or contains(@class,"ui-messages-warn") or contains(@class,"ui-message-error")]')


def describe_condition(method):
    """Best-effort description of an expected_conditions predicate (name + captured locator/value)."""
    name = getattr(method, '__qualname__', repr(method)).split('.<locals>')[0]
    args = []
    for cell in getattr(method, '__closure__', None) or ():
        try:
            value = cell.cell_contents
        except ValueError:
            continue
        if isinstance(value, (str, tuple, int, float)):
            args.append(repr(value))
    return '%s(%s)' % (name, ', '.join(args))


def collect_page_diagnostics(driver):
    lines = []

    def safe(label, fn):
        try:
            lines.append('%s: %s' % (label, fn()))
        except WebDriverException as ex:
            lines.append('%s: <unavailable: %s>' % (label, ex.msg))

    safe('Current URL', lambda: driver.current_url)
    safe('Page title', lambda: driver.title)

    def messages():
        texts = [e.text.strip() for e in driver.find_elements(By.XPATH, MESSAGE_XPATH) if e.is_displayed()]
        texts = [t.replace('\n', ' | ') for t in texts if t]
        return texts or 'none'
    safe('Visible messages', messages)

    def loading_dialog():
        found = driver.find_elements(By.ID, 'loadingDialog')
        return found[0].is_displayed() if found else 'not on page'
    safe('Loading dialog visible', loading_dialog)

    def console_errors():
        logs = driver.get_log('browser')
        errors = [entry['message'] for entry in logs if entry.get('level') == 'SEVERE']
        return errors[-5:] or 'none'
    safe('Browser console errors', console_errors)

    return lines


def save_failure_artifacts(driver, label):
    try:
        base = failure_artifact_base(label)
        driver.save_screenshot(base + '.png')
        with open(base + '.html', 'w', encoding='utf-8') as page_file:
            page_file.write(driver.page_source)
        return base + '.{png,html}'
    except (WebDriverException, OSError) as ex:
        return '<not saved: %s>' % ex


class DiagnosticWebDriverWait(WebDriverWait):

    def until(self, method, message=''):
        try:
            return super().until(method, message)
        except TimeoutException as ex:
            raise self._diagnostic_timeout(ex, 'until', method, message) from None

    def until_not(self, method, message=''):
        try:
            return super().until_not(method, message)
        except TimeoutException as ex:
            raise self._diagnostic_timeout(ex, 'until_not', method, message) from None

    def _diagnostic_timeout(self, ex, mode, method, message):
        condition = describe_condition(method)
        details = ['Timed out after %ss waiting %s %s' % (self._timeout, mode, condition)]
        if message:
            details.append(message)
        details.extend(collect_page_diagnostics(self._driver))
        details.append('Artifacts: %s' % save_failure_artifacts(self._driver, condition))
        return TimeoutException('\n  '.join(details), ex.screen, ex.stacktrace)
