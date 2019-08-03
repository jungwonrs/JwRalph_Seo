import random


class randomCh():
    def getNodeNumber(self,number):
        print("random %s" %number)
        return number

    def getSeed(self, seed):
        return seed

    def getRange(self, range):
        print("range %s" %range)
        return range

    def Calcuate(self):
        r_random = random.randrange(1, self.getRange(self))




