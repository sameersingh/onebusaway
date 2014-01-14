from __future__ import division
import numpy as np
from common import *
from sklearn import linear_model

def main():
    np.set_printoptions(threshold=np.nan)
    
    feature_names = get_feature_names()
    x_train, y_train,  = get_data("training.dat")

    # run linear regression with l1 regularization
    clf = linear_model.Lasso(alpha=1000)
    clf.fit(x_train, y_train)
    #print clf.alpha_

    w = clf.coef_
    y_hat_train = x_train.dot(w)

    rmse_our_train, rmse_oba_train = get_rmse(y_train, y_hat_train)
    
    x_test, y_test = get_data("test.dat")
    y_hat_test = x_test.dot(w)
    
    rmse_our_test, rmse_oba_test = get_rmse(y_test, y_hat_test)
 
    print "RMSE OUR Train ", rmse_our_train
    print "RMSE OBA Train ", rmse_oba_train
    print "RMSE OUR Test ", rmse_our_test
    print "RMSE OBA Test ", rmse_oba_test

    save_scatter_plot(y_train, y_hat_train, "train")
    save_scatter_plot(y_test, y_hat_test, "test")
    
    build_output_files(y_hat_train, y_hat_test, y_train, y_test)
    print_weights(w, feature_names);
    report_range(y_train)
    report_range(y_test)
    
if __name__ == '__main__':
    main()