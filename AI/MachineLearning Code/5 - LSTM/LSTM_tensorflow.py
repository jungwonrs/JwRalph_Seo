#From https://github.com/hunkim/DeepLearningZeroToAll/blob/master/lab-12-1-hello-rnn.py

import os
os.environ['TF_CPP_MIN_LOG_LEVEL']='2'
import tensorflow as tf
import numpy as np

idx2char = ['h', 'i', 'e', 'l', 'o']

x_data= [[0, 1, 0, 2, 3, 3]]   # hihell

#input
x_one_hot = [[[1, 0, 0, 0, 0],   # h 0
              [0, 1, 0, 0, 0],   # i 1
              [1, 0, 0, 0, 0],   # h 0
              [0, 0, 1, 0, 0],   # e 2
              [0, 0, 0, 1, 0],   # l 3
              [0, 0, 0, 1, 0]]]  # l 3

y_data = [[1, 0, 2, 3, 3, 4]] # ihello

num_classes = 5
input_dim = 5   # number of input array, One-hot size
hidden_size = 5 # output from the LSTM
batch_size = 1 # number of sentence
sequence_lenght = 6 # |ihello| == 6
learning_rate = 0.1

X = tf.placeholder(
    tf.float32, [None, sequence_lenght, input_dim]
) # None = batch_size, None means; don't care of batch_size

Y = tf.placeholder(tf.int32, [None, sequence_lenght])


# RNN model
# cell = rnn_cell.BasicRNNCell(rnn_size)
# cell = rnn_cell.BasicLSTMCell(rnn_size)
# cell = rnn_cell.GRUCell(rnn_size)
# rnn_size = number of output

cell = tf.contrib.rnn.BasicLSTMCell(num_units=hidden_size, state_is_tuple=True)


initial_state = cell.zero_state(batch_size, tf.float32)

# start training
outputs, _states = tf.nn.dynamic_rnn(
    cell, X, initial_state=initial_state, dtype=tf.float32
)

X_for_fc = tf.reshape(outputs, [-1, hidden_size])
outputs = tf.contrib.layers.fully_connected(
    inputs=X_for_fc, num_outputs=num_classes, activation_fn=None)

outputs = tf.reshape(outputs, [batch_size, sequence_lenght, num_classes])

weights = tf.ones([batch_size, sequence_lenght])

sequence_loss = tf.contrib.seq2seq.sequence_loss(
    logits = outputs, targets = Y, weights = weights) #logits = prediction, targets = goal (true data), weights = importance of each data (1,1,1,1,1)

loss = tf.reduce_mean(sequence_loss)
train = tf.train.AdamOptimizer(learning_rate= learning_rate).minimize(loss)

prediction = tf.argmax(outputs, axis=2)

with tf.Session() as sess:
    sess.run(tf.global_variables_initializer())
    for i in range(50):
        l, _ = sess.run([loss, train], feed_dict={X: x_one_hot, Y: y_data})
        result = sess.run(prediction, feed_dict={X: x_one_hot})
        print(i, "loss:", l, "prediction: ", result, "true Y: ", y_data)

        result_str = [idx2char[c] for c in np.squeeze(result)]
        print("\tPrediction str: ", ''.join(result_str))