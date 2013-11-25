from __future__ import division
import numpy as np
import matplotlib.pyplot as plt
from sklearn import linear_model
from common import *

def main():
    np.set_printoptions(threshold=np.nan)
    
    x_train, y_train,  = get_data("training.dat")
    
    n_alphas = 100
    alphas = np.logspace(-3, 3, n_alphas)
    clf = linear_model.Ridge(fit_intercept=True)
    
    i = 0
    coefs = []
    for a in alphas:
        clf.set_params(alpha=a)
        clf.fit(x_train, y_train)
        coefs.append(clf.coef_)
        i+=1
        print "done", i
     
    ax = plt.gca()
    ax.set_color_cycle(['b', 'r', 'g', 'c', 'k', 'y', 'm'])

    ax.plot(alphas, coefs)
    ax.set_xscale('log')
    ax.set_xlim(ax.get_xlim()[::-1])  # reverse axis
    plt.xlabel('alpha')
    plt.ylabel('weights')
    plt.title('Ridge coefficients as a function of the regularization')
    plt.axis('tight')
    plt.show()
    #TODO: add legend


if __name__ == '__main__':
    main()