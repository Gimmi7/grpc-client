# grpc-client

***
## client deadline
```aidl
//客户端设置deadline
public final S withDeadlineAfter(long duration, TimeUnit unit) {
        return this.build(this.channel, this.callOptions.withDeadlineAfter(duration, unit));
    }
//抽象的
protected abstract S build(Channel var1, CallOptions var2);
//实现
 @Override
    protected ScenarioBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ScenarioBlockingStub(channel, callOptions);
    }
    
    
//StreamObserver.onNext()的实现
 public void onNext(T value) {
            this.call.sendMessage(value);
        }
        
        
//StreamObserver.onComplete()的实现
public void onCompleted() {
            this.call.halfClose();
        }
 public final void halfClose() {
        if (!this.transportState().isOutboundClosed()) {
            this.transportState().setOutboundClosed();
            this.endOfMessages();
        }
 protected final void endOfMessages() {
        this.framer().close();
    }
 public void close() {
        if (!this.isClosed()) {
            this.closed = true;
            if (this.buffer != null && this.buffer.readableBytes() == 0) {
                this.releaseBuffer();
            }

            this.commitToSink(true, true);
        }
 private void commitToSink(boolean endOfStream, boolean flush) {
        WritableBuffer buf = this.buffer;
        this.buffer = null;
        this.sink.deliverFrame(buf, endOfStream, flush, this.messagesBuffered);
        this.messagesBuffered = 0;
    }
    }            

    }  
    
    
//StreamObserver.onError的实现
public void onError(Throwable t) {
            this.call.cancel("Cancelled by client with StreamObserver.onError()", t);
        }
public void cancel(@Nullable String message, @Nullable Throwable cause) {
        if (message == null && cause == null) {
            cause = new CancellationException("Cancelled without a message or cause");
            log.log(Level.WARNING, "Cancelling without a message or cause is suboptimal", (Throwable)cause);
        }

        if (!this.cancelCalled) {
            this.cancelCalled = true;

            try {
                if (this.stream != null) {
                    Status status = Status.CANCELLED;
                    if (message != null) {
                        status = status.withDescription(message);
                    } else {
                        status = status.withDescription("Call cancelled without message");
                    }

                    if (cause != null) {
                        status = status.withCause((Throwable)cause);
                    }

                    this.stream.cancel(status);
                }
            } finally {
                this.removeContextListenerAndCancelDeadlineFuture();
            }

        }
    }   
    
 public final void cancel(Status reason) {
        Preconditions.checkArgument(!reason.isOk(), "Should not cancel with OK status");
        this.cancelled = true;
        this.abstractClientStreamSink().cancel(reason);
    }
 public void cancel(Status status) {
            NettyClientStream.this.writeQueue.enqueue(new CancelClientStreamCommand(NettyClientStream.this.transportState(), status), true);
        }
 @CanIgnoreReturnValue
    ChannelFuture enqueue(WriteQueue.QueuedCommand command, boolean flush) {
        Preconditions.checkArgument(command.promise() == null, "promise must not be set on command");
        ChannelPromise promise = this.channel.newPromise();
        command.promise(promise);
        this.queue.add(command);
        if (flush) {
            this.scheduleFlush();
        }

        return promise;
    }                               

```
