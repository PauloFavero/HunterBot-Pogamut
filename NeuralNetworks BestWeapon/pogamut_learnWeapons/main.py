# Load the Pandas libraries with alias 'pd'
import pandas as pd
from sklearn.model_selection import train_test_split

# Read data from file 'filename.csv'
# (in the same directory that your python process is based)
# Control delimiters, rows, column names with read_csv (see later)

def prepareData(filePath):
    data = pd.read_csv(filePath)
    xFeatures = data.drop(['damage', 'weaponName'], axis=1)
    yOutput = data['damage']
    X_train, X_test, y_train, y_test = train_test_split(xFeatures, yOutput,test_size=0.2)
    print("\nX_train:\n")
    print(X_train.head())
    print(X_train.shape)

    print("\nX_test:\n")
    print(X_test.head())
    print(X_test.shape)

    print("\nY_train:\n")
    print(y_train.head())
    print(y_train.shape)

    print("\nY_test:\n")
    print(y_test.head())
    print(y_test.shape)


prepareData("data\AssaultRiflePickup.csv")

