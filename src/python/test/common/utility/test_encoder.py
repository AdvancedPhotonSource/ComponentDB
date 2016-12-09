from cgitb import enable
from unittest import TestCase

from cdb.common.utility import encoder
from cdb.common.utility.encoder import Encoder

class TestEncoder(TestCase):
    def test_encoder(self):
        encoder = Encoder()
        someData = "Hello World"
        encoded = encoder.encode(someData)

        self.assertEqual(someData, encoder.decode(encoded), "Encoder failed to decode/encode data")