import unittest
import Was_Run


class TddTest(unittest.TestCase):

    def was_run_test(self):
        test = Was_Run.WasRun("testMethod")
        self.assertIsNone(test.wasRun)
        test.run()
        self.assertEqual(test.wasRun, 1)


if __name__ == '__main__':
    unittest.main()
