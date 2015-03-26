#import <Cordova/CDV.h>
#import <netinet/in.h>
#import <arpa/inet.h>

@interface CDVDiscovery : CDVPlugin <NSNetServiceBrowserDelegate, NSNetServiceDelegate>

@property CDVInvokedUrlCommand *command;
@property NSNetServiceBrowser *netServiceBrowser;
@property bool SEARCHING;
@property NSString *serviceName;
@property NSNetService *service;

- (void)startDiscovery:(CDVInvokedUrlCommand*)command;
- (void)stopDiscovery:(CDVInvokedUrlCommand*)command;
- (void)resolve:(CDVInvokedUrlCommand*)command;

@end
