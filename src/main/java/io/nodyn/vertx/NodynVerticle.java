package io.nodyn.vertx;

import io.nodyn.Callback;
import io.nodyn.CallbackResult;
import io.nodyn.ExitHandler;
import io.nodyn.Nodyn;
import io.nodyn.runtime.NodynConfig;
import io.nodyn.runtime.RuntimeFactory;
import org.vertx.java.core.Future;
import org.vertx.java.platform.Verticle;

public class NodynVerticle extends Verticle {

    private Nodyn nodyn;

    @Override
    public void start(final Future<Void> startedResult) {
        RuntimeFactory factory = RuntimeFactory.init(getClass().getClassLoader(), RuntimeFactory.RuntimeType.DYNJS);

        final String main = container.config().getField("main");
        if ( main == null || "".equals( main ) ) {
            startedResult.setFailure( new IllegalArgumentException( "main cannot be empty" ) );
            return;
        }

        NodynConfig config = new NodynConfig( new String[] { main } );

        this.nodyn = factory.newRuntime( vertx, config );
        try {
            this.nodyn.setExitHandler(new ExitHandler() {
                @Override
                public void reallyExit(int i) {
                    NodynVerticle.this.stop();
                }
            });
            this.nodyn.runAsync(new Callback() {
                @Override
                public Object call(CallbackResult callbackResult) {
                    if (callbackResult.isError()) {
                        startedResult.setFailure(callbackResult.getError());
                    } else {
                        startedResult.setResult(null);
                    }
                    return null;
                }
            });
        } catch (Throwable throwable) {
            startedResult.setFailure(throwable);
        }
    }

    @Override
    public void stop() {
    }
}
