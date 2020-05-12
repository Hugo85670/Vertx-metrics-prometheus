package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.micrometer.PrometheusScrapingHandler;

public class ServerVerticle extends AbstractVerticle
{
	@Override
	public void start(Promise<Void> startPromise) throws Exception
	{
		int port = 8080;

		if (System.getenv("PORT") != null)
			port = Integer.parseInt(System.getenv("PORT"));

		Router router = Router.router(vertx);
		router.route("/*").handler(rc -> {
			respondWithOk(rc, "application/json", "{\"message\":\"Hello World !\"}");
		});

		vertx.createHttpServer().requestHandler(router).listen(port);
	}

	public static void respondWithOk(RoutingContext ctx, String contentType, String content)
	{
		respondWith(ctx, 200, contentType, content);
	}

	public static void respondWith(RoutingContext ctx, int statusCode, String contentType, String content)
	{
		ctx.request()
			.response() //
			.putHeader("content-type", contentType) //
			.setStatusCode(statusCode)
			.end(content);
	}
}
