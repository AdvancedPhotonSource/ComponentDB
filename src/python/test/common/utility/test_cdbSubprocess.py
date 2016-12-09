from unittest import TestCase

from cdb.common.utility.cdbSubprocess import CdbSubprocess


class TestCdbSubprocess(TestCase):
    def test_subprocess(self):
        p = CdbSubprocess('ls -l', useExceptions=False)
        p.run()
        self.assertIsNotNone(p.getStdOut())
        self.assertEqual('',p.getStdErr())
        self.assertEqual(0, p.getExitStatus())
