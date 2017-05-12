import numpy as np

x = np.array(([1, 1, 1, 1],
              [0, 1, 1, 1],
              [1, 0, 1, 1],
              [1, 1, 1, 0],
              [1, 1, 1, 1],
              [1, 0, 0, 0],
              [1, 1, 1, 1],
              [0, 1, 1, 0],
              [1, 1, 1, 1],
              [0, 0, 0, 0],
              [1, 1, 1, 1],))
y = np.array(([0], [1], [1], [1], [0], [1], [0], [1], [0], [0], [0]))


def sigmoid(z):
    s = 1.0 / (1.0 + np.exp(-1.0 * z))
    return s

w1 = np.random.rand(4, 1)
w2 = np.random.rand(4, 1)
w3 = np.random.rand(2, 1)
stop = 2101010
for i in range(stop):



    matrix1 = np.dot(x, w1)
    matrix2 = np.dot(x, w2)

    temp1 = []
    temp2 = []
    for num in matrix1:
        temp1.append(sigmoid(num))
    for num in matrix2:
        temp2.append(sigmoid(num))
    temp1 = np.reshape(temp1, (-1,1))
    temp2 = np.reshape(temp2, (-1,1))

    x_data = np.concatenate((temp1, temp2), axis=1)
    matrix3 = np.dot(x_data, w3)
    hypo = sigmoid(matrix3)
    cost = -y * np.log(hypo) - (1 - y) * np.log(1 - hypo)

    error = cost.sum() / len(y)
    lr = 0.00001
    x_t = x.transpose()
    x_data_t = x_data.transpose()



    new_w1 = w1 - lr * (x_t * w3[0])
    new_w2 = w2 - lr * (x_t * w3[1])
    new_w3 = w3 - lr * x_data_t
    size = new_w1.size/len(w1)


    w1 = []
    w2 = []
    w3 = []

    for i in new_w1:
        w1.append(sum(i)/size)
    for i in new_w2:
        w2.append(sum(i) / size)
    for i in new_w3:
        w3.append(sum(i) / size)


    w1 = np.reshape(w1, (-1, 1))
    w2 = np.reshape(w2, (-1, 1))
    w3 = np.reshape(w3, (-1, 1))


    print("error", error)
    print("w1", w1)
    print("w2", w2)
    print("w3", w3)
    print("---------------")