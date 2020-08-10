#include "serve.h"

#include <SDL2/SDL_events.h>
#include <SDL2/SDL_mutex.h>
#include <SDL2/SDL_thread.h>

#include "config.h"
#include "events.h"
#include "util/log.h"
#include "util/net.h"

# define SOCKET_ERROR (-1)

void
serve_init(struct serve* serve, char *protocol, uint32_t ip, uint16_t port) {
    serve->protocol = protocol;
    serve->ip = ip;
    serve->port = port;
}

bool
serve_start(struct serve* serve) {
    LOGD("Starting serve thread");

    socket_t Listensocket;
    socket_t ClientSocket;

    Listensocket = net_listen(serve->ip, serve->port, 1);
    if (Listensocket == INVALID_SOCKET) {
        LOGI("Listen error");
        net_close(Listensocket);
        return 0;
    }

    LOGI("Waiting for a client to connect");

    ClientSocket = net_accept(Listensocket);
    if (ClientSocket == INVALID_SOCKET) {
        LOGI("Client error");
        net_close(Listensocket);
        return 0;
    }

    LOGI("Client found");

    net_close(Listensocket);

    serve->socket = ClientSocket;

    return true;
}

bool
serve_push(struct serve* serve, const AVPacket *packet) {
    if (net_send(serve->socket, packet->data, packet->size) == SOCKET_ERROR) {
        LOGI("Client lost");
        net_close(serve->socket);
        return false;
    }
    return true;
}