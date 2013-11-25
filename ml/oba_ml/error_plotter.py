from __future__ import division
import numpy as np
import matplotlib.pyplot as plt

def main():
    
    #error("oba_our_errors_train.dat", "train_error.png", 0, "Train")
    error("oba_our_errors_test.dat", "test_error.png", 0)


def error(filename, output_filename, sched_error_value):
    data = np.loadtxt(filename)
    k = np.arange(1,data.shape[0]+1,1)
    oba_error = np.array(data[:,0]).reshape(data.shape[0],1)
    mode_error = np.array(data[:,1]).reshape(data.shape[0],1)
    #sched_error = np.empty(data.shape[0]).reshape(data.shape[0],1)
    #sched_error.fill(sched_error_value)
    fig = plt.figure()
    #fig.suptitle("Error vs. K - {} set".format(type), fontsize=13, fontweight='bold')
    fontsize = 14
    ax = plt.gca()
    for tick in ax.xaxis.get_major_ticks():
        tick.label1.set_fontsize(fontsize)
        tick.label1.set_fontweight('bold')
    for tick in ax.yaxis.get_major_ticks():
        tick.label1.set_fontsize(fontsize)
        tick.label1.set_fontweight('bold')
        
    plt.plot(k, oba_error, "b", lw=3)
    plt.plot(k, mode_error, "r", lw=3)
    #plt.plot(k, sched_error, "b")
    plt.xlabel("K = number of segments", fontweight='bold')
    plt.ylabel("RMSE in seconds", fontweight='bold')
    
    plt.legend(['OBA', 'OUR'], loc="lower right")
    plt.savefig(output_filename)
    plt.show()

     
if __name__ == '__main__':
    main()