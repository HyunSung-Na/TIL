import test_case


class WasRun(test_case.TestCase):
    def __init__(self, name):
        self.wasSetUp = 1
        self.wasRun = None
        test_case.TestCase.__init__(self.name)

    def setUp(self):
        pass
