from __future__ import division
import numpy as np
import matplotlib.pyplot as plt

def get_feature_names():
    return open("features_names.txt").read().splitlines()

def get_data(filename):
    
    data = np.loadtxt(filename)
    y = np.array(data[:,data.shape[1]-1]).reshape(data.shape[0],1)
    x = np.array(data[:,0:data.shape[1]-1])
    return (x, y)
 
def get_rmse(y, y_hat):
    err_our = y_hat - y
    rmse_our = np.sqrt(np.mean(err_our**2))
    err_oba =  y 
    rmse_oba = np.sqrt(np.mean(err_oba**2))
    return (rmse_our, rmse_oba)
       

def save_scatter_plot(y, y_hat, set_name):
    fig = plt.figure()
    fig.suptitle("Y vs. Y_hat - {} set".format(set_name), fontsize=13, fontweight='bold')
    plt.scatter(y, y_hat)
    plt.xlabel("y")
    plt.ylabel("y_hat")
    plt.savefig("y_vs_yhat_{}.png".format(set_name))   
 
def save_histogram(y_yhat, set_name):
    fig = plt.figure()
    fig.suptitle("Y-Yhat Histogram - {} set".format(set_name))
    plt.hist(y_yhat, 10)
    plt.savefig("y_yhat_histogram_{}.png".format(set_name))
    
    build_files(y_hat_train, y_hat_test, y, y_test)
    print_weights_feature_names(w, feature_names);
    report_range(y)
    
def build_output_files(y_hat_train, y_hat_test, y, y_test):
    
    feature_names_train = open("labels_training.txt").read().splitlines()
    appended_train = np.column_stack((feature_names_train, y_hat_train))
    np.savetxt("y_hat_train.csv", appended_train, delimiter=",", fmt="%s")
    
    feature_names_test = open("labels_test.txt").read().splitlines()
    appended_test = np.column_stack((feature_names_test, y_hat_test))
    np.savetxt("y_hat_test.csv", appended_test, delimiter=",", fmt="%s")
    
    appended_train_y = np.column_stack((feature_names_train, y))
    np.savetxt("y.csv", appended_train_y, delimiter=",", fmt="%s")
    
    appended_test_y = np.column_stack((feature_names_test, y_test))
    np.savetxt("y_test.csv", appended_test_y, delimiter=",", fmt="%s")

def print_weights(w, feature_names):
    print np.column_stack((w, feature_names))

def report_range(y):
    print "ymin {} - index {}".format(y[np.argmin(y[:,0]),0], np.argmin(y[:,0]))
    print "ymax {} - index {}".format(y[np.argmax(y[:,0]),0], np.argmax(y[:,0]))