import unittest
import Was_Run


class TddTest(unittest.TestCase):
    def setUp(self):
        self.test = Was_Run.WasRun('testMethod')

    def was_run_test(self):
        self.test.run()
        self.assertEqual(self.test.wasRun, 1)

    def testSetUp(self):
        self.test.run()
        self.assertTrue(self.test.wasSetUp)

if __name__ == '__main__':
    unittest.main()
