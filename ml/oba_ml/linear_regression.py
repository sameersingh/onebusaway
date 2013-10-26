from __future__ import division
import numpy as np
import scipy.io as io
import matplotlib.pyplot as plt
from sklearn import linear_model

def main():
    np.set_printoptions(threshold=np.nan)
    training_data = np.loadtxt("features_training.dat")
    feature_names = open("features_names.txt").read().splitlines()
    y_train = np.array(training_data[:,training_data.shape[1]-1]).reshape(training_data.shape[0],1)
    x_mode_train = np.array(training_data[:,0:training_data.shape[1]-1])
    clf = linear_model.LinearRegression()
    clf.fit (x_mode_train, y_train)
    w = clf.coef_.reshape(clf.coef_.shape[1],1) 
    y_hat_mode_train = x_mode_train.dot(w)
    err_mode_train = y_hat_mode_train - y_train
    rmse_mode_train = np.sqrt(np.mean(err_mode_train**2))
    
    y_hat_oba_train = np.zeros(y_train.shape[0]).reshape(y_train.shape[0],1)
    err_oba_train =  y_hat_oba_train - y_train 
    rmse_oba_train = np.sqrt(np.mean(err_oba_train**2))
    
    test_data = np.loadtxt("features_test.dat")
    y_test = np.array(test_data[:,test_data.shape[1]-1]).reshape(test_data.shape[0],1)
    x_mode_test = np.array(test_data[:,0:test_data.shape[1]-1])
    y_hat_mode_test = x_mode_test.dot(w)
    err_mode_test = y_hat_mode_test - y_test
    rmse_mode_test = np.sqrt(np.mean(err_mode_test**2))
    
    y_hat_oba_test = np.zeros(y_test.shape[0]).reshape(y_test.shape[0],1)
    err_oba_test = y_hat_oba_test - y_test
    rmse_oba_test = np.sqrt(np.mean(err_oba_test**2))

    print "RMSE Mode Train ", rmse_mode_train
    print "RMSE OBA Train ", rmse_oba_train
    print "RMSE Mode Test ", rmse_mode_test
    print "RMSE OBA Test ", rmse_oba_test


    #plt.scatter(y_train, y_hat_mode_train )
    #plt.show()
    print np.column_stack((w, feature_names))   
    
    build_file(y_hat_mode_train, y_hat_mode_test)
    
    
 

    
def build_file(y_hat_mode_train, y_hat_mode_test):
    feature_names_train = open("labels_training.txt").read().splitlines()
    appended_train = np.column_stack((feature_names_train, y_hat_mode_train))
    np.savetxt("features_y_hat_train.csv", appended_train, delimiter=",", fmt="%s")
    feature_names_test = open("labels_test.txt").read().splitlines()
    appended_test = np.column_stack((feature_names_test, y_hat_mode_test))
    np.savetxt("features_y_hat_test.csv", appended_test, delimiter=",", fmt="%s")


if __name__ == '__main__':
    main()