# Load the Pandas libraries with alias 'pd'

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split

from pogamut_learnWeapons.network import Neural_Network

NN_AssaultRifle = Neural_Network(3, 1, 50)
assaultRiflePath = "data\AssaultRiflePickup.csv"

NN_BioRifle = Neural_Network(3, 1, 50)
bioRiflePath = "data\BioRiflePickup.csv"

NN_Flak = Neural_Network(3, 1, 50)
flakPath = "data\FlakCannonPickup.csv"

NN_LinkGun = Neural_Network(3, 1, 50)
linkgunPath = "data\LinkGunPickup.csv"

NN_Minigun = Neural_Network(3, 1, 50)
minigunPath = "data\MinigunPickup.csv"

NN_RocketLauncher = Neural_Network(3, 1, 50)
rocketLauncherPath = "data\RocketLauncherPickup.csv"

NN_ShockRifle = Neural_Network(3, 1, 50)
shockRiflePath = "data\ShockRiflePickup.csv"

NN_SniperRifle = Neural_Network(3, 1, 50)
sniperRiflePath = "data\SniperRiflePickup.csv"

# list of networks
networksList = [NN_AssaultRifle, NN_BioRifle, NN_Flak, NN_LinkGun, NN_Minigun, NN_RocketLauncher, NN_ShockRifle, NN_SniperRifle]
pathList = [assaultRiflePath,bioRiflePath,flakPath,linkgunPath,minigunPath,rocketLauncherPath,shockRiflePath,sniperRiflePath]
nameList = ["AssaultRifle", "BioRifle", "FlakCannon", "LinkGun", "MiniGun", "RocketLauncher", "ShockRifle", "SniperRifle"]
for i in range(len(networksList)):
    NN = networksList[i]
    X_train, X_test, y_train, y_test = NN.prepareData(pathList[i])

    for j in range(1000):  # trains the NN 1,000 times
        print("# " + str(j) + "\n")
        #print("Input (scaled): \n" + str(X_train))
        #print("Actual Output: \n" + str(y_train))
        #print("Predicted Output: \n" + str(NN.forward(X_train)))
        print("**************")
        print("Loss: " + str(np.mean(np.square(y_train - NN.forward(X_train)))))  # mean sum squared loss
        print("\n")
        NN.train(X_train, y_train)

    NN.saveWeights(nameList[i])
    NN.predict(X_test)
    print("***********************************************************************************************")
