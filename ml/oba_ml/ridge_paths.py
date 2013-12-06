from __future__ import division
import numpy as np
import matplotlib.pyplot as plt
from sklearn import linear_model
from common import *

def main():
    np.set_printoptions(threshold=np.nan)
    
    x_train, y_train,  = get_data("training.dat")
    
    n_alphas = 100
    alphas = np.logspace(-5, 5, n_alphas)
    clf = linear_model.Ridge(fit_intercept=True)
    
    i = 0
    coefs = []
    for a in alphas:
        clf.set_params(alpha=a)
        clf.fit(x_train, y_train)
        coefs.append(pick_coefs(clf.coef_))
        i+=1
        print "done", i
     
    ax = plt.gca()
    ax.set_color_cycle(['b', 'r', 'g', 'c', 'k', 'y', 'm'])

    ax.plot(alphas, np.squeeze(coefs))
    ax.set_xscale('log')
    ax.set_xlim(ax.get_xlim()[::-1])  # reverse axis
    plt.xlabel('alpha')
    plt.ylabel('weights')
    plt.title('Ridge Coefficients as a function of the Regularization')
    plt.axis('tight')
    plt.legend(['6-8am', '8-10am', 'monday', 'saturday', 'downtown', 'udistrict', 'bellevue'], loc="upper left")
    plt.savefig('ridge_path.png')
    plt.show()

def pick_coefs(coef):
    coefficients = np.zeros(7).reshape(1,7)
    coefficients[0][0] = coef[0][5]
    coefficients[0][1] = coef[0][6]
    coefficients[0][2] = coef[0][14]
    coefficients[0][3] = coef[0][19]
    coefficients[0][4] = coef[0][660]    
    coefficients[0][5] = coef[0][669]
    coefficients[0][6] = coef[0][672]
    return coefficients

if __name__ == '__main__':
    main()