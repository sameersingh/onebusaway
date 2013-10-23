from __future__ import division
import numpy as np
import matplotlib.pyplot as plt

def main():
    
    error("oba_mode_errors_train.dat", "train_error.png", "Train")
    error("oba_mode_errors_test.dat", "test_error.png", "Test")


def error(filename, output_filename, type):
    data = np.loadtxt(filename)
    k = np.arange(1,data.shape[0]+1,1)
    oba_error = np.array(data[:,0]).reshape(data.shape[0],1)
    mode_error = np.array(data[:,1]).reshape(data.shape[0],1)
    sched_error = np.array(data[:,2]).reshape(data.shape[0],1)
    fig = plt.figure()
    fig.suptitle("Error vs. K - {}".format(type), fontsize=13, fontweight='bold')
    plt.plot(k, oba_error, "r")
    plt.plot(k, mode_error, "g")
    plt.plot(k, sched_error, "b")
    plt.xlabel("k")
    plt.legend(['OBA', 'MODE', 'SCHED'], loc="upper left")
    plt.savefig(output_filename)
    plt.show()

     
if __name__ == '__main__':
    main()