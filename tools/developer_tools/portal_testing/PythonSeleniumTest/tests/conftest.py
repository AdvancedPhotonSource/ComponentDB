"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
import pytest

from cdbSeleniumModules import diagnosticWait


def pytest_configure(config):
    config.addinivalue_line(
        "markers",
        "mqtt: stateful MQTT verification; run serially via sbin/cdb_mqtt_test.sh, "
        "deselect from parallel runs with -m \"not mqtt\"")


def pytest_runtest_setup(item):
    # Timeout screenshots/HTML get the same prefix as this test's failure log.
    diagnosticWait.current_test_label = '%s.%s' % (item.cls.__name__, item.name) if item.cls else item.name


@pytest.hookimpl(hookwrapper=True)
def pytest_runtest_makereport(item, call):
    outcome = yield
    report = outcome.get_result()
    # Unittest-style tests run setUp/tearDown inside "call"; keep setup/teardown errors too.
    if report.failed or report.outcome == 'rerun':
        write_failure_log(item, report)


def write_failure_log(item, report):
    status = 'RERUN' if report.outcome == 'rerun' else 'FAILED'
    try:
        path = diagnosticWait.failure_artifact_base('%s-%s' % (report.when, status.lower())) + '.log'
        with open(path, 'w', encoding='utf-8') as log_file:
            log_file.write('%s %s (phase: %s, %.1fs)\n\n' % (status, report.nodeid, report.when, report.duration))
            log_file.write(report.longreprtext + '\n')
            for section_name, content in report.sections:
                log_file.write('\n%s\n%s\n' % (('-' * 20 + ' %s ' % section_name + '-' * 20), content))
    except OSError as ex:
        print('Could not write failure log for %s: %s' % (report.nodeid, ex))
