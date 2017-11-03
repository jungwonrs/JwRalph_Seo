#Fail....maybe(?)

import numpy as np

h = [1, 0, 0, 0, 0]
i = [0, 1, 0, 0, 0]
e = [0, 0, 1, 0, 0]
l = [0, 0, 0, 1, 0]
o = [0, 0, 0, 0, 1]

x_data = np.array([h,i,h,e,l,l])

y_data = np.array([i,h,e,l,l,o])

def sigmoid(x):
    return 1.0/ (1.0+np.exp(-x))

def tanh(x):
    return 2 * sigmoid(2*x)-1

lr = 0.00001

#forward
input_activation = []
input_gate = []
forget_gate = []
output_gate = []

internal_state = 0
output = 0

wa = 0.1
wi = 0.2
wf = 0.3
wo = 0.4

ba = 0.5
bi = 0.6
bf = 0.7
bo = 0.8

index= 0

outputList =[]
stateList = []

while True:
    a = tanh(wa * x_data[index] + wa * output + ba)
    i = sigmoid(wi * x_data[index] + wi * output + bi)
    f = sigmoid(wf * x_data[index] + wf * output + bf)
    o = sigmoid(wo * x_data[index] + wo * output + bo)
    state = a * i + f * internal_state
    out = tanh(state) * o

    #print(out)
    #print("----------")

    internal_state = state
    output = out

    outputList.append(output)
    input_activation.append(a)
    input_gate.append(i)
    forget_gate.append(f)
    output_gate.append(o)
    stateList.append(state)

    index += 1
    print(index)
    print(output)
    if index > (len(x_data)-1):
        break;


#backward
d_out = 0
d_internal_state = 0
d_input_activation = 0
d_input_gate = 0
d_forget_gate = 0
d_output_gate = 0
d_x = 0
di_out = 0

d_index = len(outputList)-1

d_internal_stateList = []

while True:
    d_out = outputList[d_index] + di_out

    d_state = d_out * output_gate[d_index]*(1-np.square(tanh(stateList[d_index]))) + d_internal_state * outputList[d_index]
    d_internal_state = d_state

    d_input_activation = d_state * input_gate[d_index] * (1 - np.square(input_activation[d_index]))

    d_input_gate = d_state * input_activation[d_index] * input_gate[d_index] * (1 - input_gate[d_index])

    d_forget_gate = d_state * stateList[d_index-1] * forget_gate[d_index] * (1-forget_gate[d_index])

    d_output_gate = d_out * tanh(stateList[d_index]) * output_gate[d_index] * (1 - output_gate[d_index])

    d_wa = np.multiply(wa, d_input_activation)
    d_wi = wi * d_input_gate
    d_wf = wf * d_forget_gate
    d_wo = wo * d_output_gate

    new_wa = wa - lr*d_wa
    new_wi = wi - lr*d_wi
    new_wf = wf - lr*d_wf
    new_wo = wo - lr*d_wo

    wa = new_wa
    wi = new_wi
    wf = new_wf
    wo = new_wo

    #print(wa)
    #print("----------")
    index += 1
    d_index -= 1
    if d_index==-1:
        break


