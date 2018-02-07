class temp():
    def __init__(self, data1, data2, data3):
        self.data1 = data1
        self.data2 = data2
        self.data3 = data3

    def make(self):
        while True:

            self.data1 += 1
            self.data2 = 2
            self.data3 = 3

            temp(self.data1, self.data2, self.data3)

            print(self.data1)

            if self.data1 == 10:
                return

temp(5,2,3).make()