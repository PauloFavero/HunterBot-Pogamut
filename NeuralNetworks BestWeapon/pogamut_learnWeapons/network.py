import numpy as np

# Load the Pandas libraries with alias 'pd'
import pandas as pd
from sklearn.model_selection import train_test_split

# Read data from file 'filename.csv'
# (in the same directory that your python process is based)
# Control delimiters, rows, column names with read_csv (see later)

def prepareData(filePath):
    data = pd.read_csv(filePath)
    xFeatures = data.drop(['damage', 'weaponName'], axis=1)
    yOutput = data.drop(['distance',"rotation","speed",'weaponName'], axis=1)
    X_train, X_test, y_train, y_test = train_test_split(xFeatures, yOutput,test_size=0.2)
    #print("\nX_train:\n")
    #print(X_train.head())
    #print(X_train.shape)

    #print("\nX_test:\n")
    #print(X_test.head())
    #print(X_test.shape)

    #print("\nY_train:\n")
    #print(y_train.head())
    #print(y_train.shape)

    #print("\nY_test:\n")
    #print(y_test.head())
    #print(y_test.shape)

    return X_train, X_test, y_train, y_test


# X = (distance, rotation, speed), y = damage
# TODO: Set a range of zero to one

X_train, X_test, y_train, y_test = prepareData("data\AssaultRiflePickup.csv")

#X = np.random.randn(number_of_samples, 3)
#y = np.random.randn(number_of_samples, 1)
#xPredicted = np.random.randn(10, 3)

# scale units
#X = X / np.amax(X, axis=0)  # maximum of X array (distance, rotation and speed)
#y = y / np.amax(y, axis=0)  # max damage between 0 and 1
#xPredicted = xPredicted / np.amax(xPredicted, axis=0)
#print(X.shape)
#print(xPredicted)

class Neural_Network(object):
    def __init__(self, in_layer_size, out_layer_size, hidden_layer_size):
        # parameters
        self.inputSize = in_layer_size
        self.outputSize = out_layer_size
        self.hiddenSize = hidden_layer_size

        # weights
        self.W1 = np.random.randn(self.inputSize, self.hiddenSize)  # (3x50) weight matrix from input to hidden layer
        self.W2 = np.random.randn(self.hiddenSize, self.outputSize)  # (50x1) weight matrix from hidden to output layer

    def forward(self, X_train):
        # forward propagation through our network
        self.z = np.dot(X_train, self.W1)  # dot product of X (input) and first set of 3x50 weights
        self.z2 = self.sigmoid(self.z)  # activation function
        self.z3 = np.dot(self.z2, self.W2)  # dot product of hidden layer (z2) and second set of 50x1 weights
        o = self.sigmoid(self.z3)  # final activation function
        return o

    def sigmoid(self, s):
        # activation function
        return 1 / (1 + np.exp(-s))

    def sigmoidPrime(self, s):
        # derivative of sigmoid
        return s * (1 - s)

    def backward(self, X, y, o):
        # backward propagate through the network
        self.o_error = y - o  # error in output
        self.o_delta = self.o_error * self.sigmoidPrime(o)  # applying derivative of sigmoid to error

        self.z2_error = self.o_delta.dot(
        self.W2.T)  # z2 error: how much our hidden layer weights contributed to output error
        self.z2_delta = self.z2_error * self.sigmoidPrime(self.z2)  # applying derivative of sigmoid to z2 error

        self.W1 += X.T.dot(self.z2_delta)  # adjusting first set (input --> hidden) weights
        self.W2 += self.z2.T.dot(self.o_delta)  # adjusting second set (hidden --> output) weights

    def train(self, X, y):
        o = self.forward(X)
        self.backward(X, y, o)


    def saveWeights(self):
        np.savetxt("w1.txt", self.W1, fmt="%s")
        np.savetxt("w2.txt", self.W2, fmt="%s")


    def predict(self):
        print("Predicted data based on trained weights: ")
        print("Input (scaled): \n" + str(X_test))
        print("Output: \n" + str(self.forward(X_test)))


NN = Neural_Network(3, 1, 50)
for i in range(1000):  # trains the NN 1,000 times
    print("# " + str(i) + "\n")
    print("Input (scaled): \n" + str(X_train))
    print("Actual Output: \n" + str(y_train))
    print("Predicted Output: \n" + str(NN.forward(X_train)))
    print("Loss: \n" + str(np.mean(np.square(y_train - NN.forward(X_train)))))  # mean sum squared loss
    print("\n")
    NN.train(X_train, y_train)

NN.saveWeights()
NN.predict()
