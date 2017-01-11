package com.fsryan.example.multiprocess.keystore.cp;

import com.fsryan.example.multiprocess.keystore.cp.ISimpleServiceCallbacks;

interface ISimpleService {
    void stopRefreshing();
    void startRefreshing();
    void registerCallbacks(ISimpleServiceCallbacks callbacks);
}
