from __future__ import division
import numpy as np
import matplotlib.pyplot as plt
from sklearn import linear_model

def main():
    np.set_printoptions(threshold=np.nan)
    
    # read training data
    training_data = np.loadtxt("training.dat")
    feature_names = open("features_names.txt").read().splitlines()
    y_train = np.array(training_data[:,training_data.shape[1]-1]).reshape(training_data.shape[0],1)
    x_train = np.array(training_data[:,0:training_data.shape[1]-1])
    
    # run linear regression
    clf = linear_model.LinearRegression()
    clf.fit (x_train, y_train)
    
    # get w
    w = clf.coef_.reshape(clf.coef_.shape[1],1) 

    # calculate RMSEs for training data
    y_hat_train = x_train.dot(w)
    err_our_train = y_hat_train - y_train
    rmse_our_train = np.sqrt(np.mean(err_our_train**2))
    err_oba_train =  y_train 
    rmse_oba_train = np.sqrt(np.mean(err_oba_train**2))
   
    # read test data
    test_data = np.loadtxt("test.dat")
    y_test = np.array(test_data[:,test_data.shape[1]-1]).reshape(test_data.shape[0],1)
    x_test = np.array(test_data[:,0:test_data.shape[1]-1])
    
    # calculate RMSEs for test data
    y_hat_test = x_test.dot(w)
    err_our_test = y_hat_test - y_test
    rmse_our_test = np.sqrt(np.mean(err_our_test**2))
    err_oba_test =  y_test
    rmse_oba_test = np.sqrt(np.mean(err_oba_test**2))

    print "RMSE OUR Train ", rmse_our_train
    print "RMSE OBA Train ", rmse_oba_train
    print "RMSE OUR Test ", rmse_our_test
    print "RMSE OBA Test ", rmse_oba_test

    save_scatter_plot(y_train, y_hat_train, "train")
    save_scatter_plot(y_test, y_hat_test, "test")
    save_histogram(y_test - y_hat_test, "test")
    
    build_files(y_hat_train, y_hat_test, y_train, y_test)
    print_weights_feature_names(w, feature_names);
    report_range(y_train)
    
def build_files(y_hat_train, y_hat_test, y_train, y_test):
    
    feature_names_train = open("labels_training.txt").read().splitlines()

    appended_train = np.column_stack((feature_names_train, y_hat_train))
    np.savetxt("y_hat_train.csv", appended_train, delimiter=",", fmt="%s")
    feature_names_test = open("labels_test.txt").read().splitlines()
    appended_test = np.column_stack((feature_names_test, y_hat_test))
    np.savetxt("y_hat_test.csv", appended_test, delimiter=",", fmt="%s")

    appended_train_y = np.column_stack((feature_names_train, y_train))
    np.savetxt("y_train.csv", appended_train_y, delimiter=",", fmt="%s")
    appended_test_y = np.column_stack((feature_names_test, y_test))
    np.savetxt("y_test.csv", appended_test_y, delimiter=",", fmt="%s")

def save_histogram(y_yhat, set_name):
    fig = plt.figure()
    fig.suptitle("Y-Yhat Histogram - {} set".format(set_name))
    plt.hist(y_yhat, 10)
    plt.savefig("y_yhat_histogram_{}.png".format(set_name))

def save_scatter_plot(y, y_hat, set_name):
    fig = plt.figure()
    fig.suptitle("Y vs. Y_hat - {} set".format(set_name), fontsize=13, fontweight='bold')
    plt.scatter(y, y_hat)
    plt.xlabel("y")
    plt.ylabel("y_hat")
    plt.savefig("y_vs_yhat_{}.png".format(set_name))
    
def print_weights_feature_names(w, feature_names):
    print np.column_stack((w, feature_names))

def report_range(y_train):
    print "ymin {} - index {}".format(y_train[np.argmin(y_train[:,0]),0], np.argmin(y_train[:,0]))
    print "ymax {} - index {}".format(y_train[np.argmax(y_train[:,0]),0], np.argmax(y_train[:,0]))


if __name__ == '__main__':
    main()