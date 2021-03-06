// **********************************************************************
//
// Copyright (c) 2003-2017 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package test.Ice.ami;

import test.Ice.ami.Test._TestIntfDisp;
import test.Ice.ami.Test.AMD_TestIntf_startDispatch;
import test.Ice.ami.Test.CloseMode;
import test.Ice.ami.Test.TestIntfException;
import test.Ice.ami.Test.PingReplyPrxHelper;

public class TestI extends _TestIntfDisp
{
    TestI()
    {
    }

    @Override
    public void
    op(Ice.Current current)
    {
    }

    @Override
    public int
    opWithResult(Ice.Current current)
    {
        return 15;
    }

    @Override
    public void
    opWithUE(Ice.Current current)
        throws TestIntfException
    {
        throw new TestIntfException();
    }

    @Override
    public void
    opWithPayload(byte[] seq, Ice.Current current)
    {
    }

    @Override
    public synchronized void
    opBatch(Ice.Current current)
    {
        ++_batchCount;
        notify();
    }

    @Override
    public synchronized int
    opBatchCount(Ice.Current current)
    {
        return _batchCount;
    }

    @Override
    public boolean supportsAMD(Ice.Current current)
    {
        return true;
    }

    @Override
    public boolean supportsFunctionalTests(Ice.Current current)
    {
        return true;
    }

    @Override
    public boolean opBool(boolean b, Ice.Current current)
    {
        return b;
    }

    @Override
    public byte opByte(byte b, Ice.Current current)
    {
        return b;
    }

    @Override
    public short opShort(short s, Ice.Current current)
    {
        return s;
    }

    @Override
    public int opInt(int i, Ice.Current current)
    {
        return i;
    }

    @Override
    public long opLong(long l, Ice.Current current)
    {
        return l;
    }

    @Override
    public float opFloat(float f, Ice.Current current)
    {
        return f;
    }

    @Override
    public double opDouble(double d, Ice.Current current)
    {
        return d;
    }

    @Override
    public void pingBiDir(Ice.Identity id, Ice.Current current)
    {
        PingReplyPrxHelper.uncheckedCast(current.con.createProxy(id)).reply();
    }

    @Override
    public synchronized boolean
    waitForBatch(int count, Ice.Current current)
    {
        while(_batchCount < count)
        {
            try
            {
                wait(5000);
            }
            catch(InterruptedException ex)
            {
            }
        }
        boolean result = count == _batchCount;
        _batchCount = 0;
        return result;
    }

    @Override
    public void
    close(CloseMode mode, Ice.Current current)
    {
        current.con.close(Ice.ConnectionClose.valueOf(mode.value()));
    }

    @Override
    public void
    sleep(int ms, Ice.Current current)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException ex)
        {
        }
    }

    @Override
    public synchronized void
    startDispatch_async(AMD_TestIntf_startDispatch cb, Ice.Current current)
    {
        _pending.add(cb);
    }

    @Override
    public synchronized void
    finishDispatch(Ice.Current current)
    {
        for(AMD_TestIntf_startDispatch cb : _pending)
        {
            cb.ice_response();
        }
        _pending.clear();
    }

    @Override
    public synchronized void
    shutdown(Ice.Current current)
    {
        //
        // Just in case a request arrived late.
        //
        for(AMD_TestIntf_startDispatch cb : _pending)
        {
            cb.ice_response();
        }
        current.adapter.getCommunicator().shutdown();
    }

    private int _batchCount;
    private java.util.List<AMD_TestIntf_startDispatch> _pending = new java.util.LinkedList<>();
}
