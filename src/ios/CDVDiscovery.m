#import "CDVDiscovery.h"
#import <Cordova/CDV.h>
#import <Foundation/Foundation.h>

@implementation CDVDiscovery

- (void)pluginInitialize {

    self.netServiceBrowser = [[NSNetServiceBrowser alloc] init];
    self.netServiceBrowser.delegate = self;
}

- (void)startDiscovery:(CDVInvokedUrlCommand *)command {

    NSString* serviceName = [command.arguments objectAtIndex:0];
    NSString* serviceType = [command.arguments objectAtIndex:1];

    if (!self.SEARCHING && serviceName && serviceType) {

        self.command = command;
        self.serviceName = serviceName;

        [self.netServiceBrowser searchForServicesOfType:serviceType inDomain:@"local"];
    }
    else {

        CDVPluginResult* pluginResult = nil;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.command.callbackId];
    }
}

- (void)stopDiscovery:(CDVInvokedUrlCommand*)command {

    if (self.netServiceBrowser) {
        [self.netServiceBrowser stop];
        self.SEARCHING = NO;
    }
}

- (void)resolve:(CDVInvokedUrlCommand *)command {

    NSString* serviceName = [command.arguments objectAtIndex:0];
    NSString* serviceType = [command.arguments objectAtIndex:1];
    NSString* serviceDomain = [command.arguments objectAtIndex:2];

    if (!self.SEARCHING && serviceName && serviceType && serviceDomain) {
        self.command = command;

        NSNetService *service;
        service = [[NSNetService alloc] initWithDomain:serviceDomain type:serviceType name:serviceName];
        [service setDelegate:self];

        [service resolveWithTimeout:5.0];
    }
    else {

        CDVPluginResult* pluginResult = nil;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.command.callbackId];
    }
}

- (void)returnResultWithError {

    CDVPluginResult* pluginResult = nil;

    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.command.callbackId];
}

- (void)returnServiceResolveData:(NSNetService *)service {

    NSData *address = service.addresses[0];

    struct sockaddr_in *socketAddress = (struct sockaddr_in *) [address bytes];

    NSString *serviceIP = [[NSString alloc] initWithUTF8String:inet_ntoa(socketAddress->sin_addr)];

    NSDictionary *pluginJSONResult = @{
                                       @"host": serviceIP,
                                       @"port": [[NSNumber alloc] initWithLong:service.port],
                                     };


    // Return plugin data
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:pluginJSONResult];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.command.callbackId];
}

- (void)returnDiscoveryResult:(NSMutableArray*)services {

    NSMutableDictionary *pluginJSONResult = [[NSMutableDictionary alloc] init];

    NSMutableArray *listOfServices = [[NSMutableArray alloc] init];

    [pluginJSONResult setObject:listOfServices forKey:@"services"];

    for (NSNetService *service in services) {

        NSMutableDictionary *serviceData = [NSMutableDictionary
                                            dictionaryWithDictionary:@{
                                                                       @"name": service.name,
                                                                       @"domain": service.domain,
                                                                       @"type": service.type
                                                                       }];
        [listOfServices addObject:serviceData];

        NSMutableArray *serviceAddressesData = [[NSMutableArray alloc] init];
        [serviceData setObject:serviceAddressesData forKey:@"addresses"];
    }

    //NSError *error = nil;
    //NSData *json;
    //json = [NSJSONSerialization dataWithJSONObject:pluginJSONResult options:NSJSONWritingPrettyPrinted error:&error];
    //NSLog(@"====> %@", [[NSString alloc] initWithData:json encoding:NSUTF8StringEncoding]);

    // Return plugin data
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:pluginJSONResult];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.command.callbackId];
}

// NSNetServiceBrowserDelegate messages

- (void)netServiceBrowserWillSearch:(NSNetServiceBrowser *)browser {

    self.SEARCHING = YES;
    NSLog(@"Service search started");
}

- (void)netServiceBrowser:(NSNetServiceBrowser *)browser
           didFindService:(NSNetService *)aNetService
               moreComing:(BOOL)moreComing {

    NSLog(@"Found service %@", aNetService.name);

    if ([aNetService.name rangeOfString:self.serviceName].location != NSNotFound) {
        [browser stop];

        self.service = aNetService;
        [self.service setDelegate:self];
        [self.service resolveWithTimeout:5.0];
    }
    else if(!moreComing) {
        [browser stop];
    }
}

- (void)netServiceBrowserDidStopSearch:(NSNetServiceBrowser *)browser {

    NSLog(@"Stopping service search");

    self.SEARCHING = NO;
}

- (void)netServiceBrowser:(NSNetServiceBrowser *)browser
             didNotSearch:(NSDictionary *)errorDict {

    [self handleError:[errorDict objectForKey:NSNetServicesErrorCode]];
}

- (void)netServiceBrowser:(NSNetServiceBrowser *)browser
         didRemoveService:(NSNetService *)aNetService
               moreComing:(BOOL)moreComing {

    if(!moreComing) {
        [browser stop];
    }
}

- (void)handleError:(NSNumber *)error {

    NSLog(@"B An error occurred. Error code = %d", [error intValue]);

    [self returnResultWithError];
}

// NSNetServiceDelegate messages

- (void)netServiceDidResolveAddress:(NSNetService *)netService {

    NSLog(@"B Service %@ resolved", netService.name);

    [self returnServiceResolveData:netService];
}

- (void)netService:(NSNetService *)netService
     didNotResolve:(NSDictionary *)errorDict {

    NSLog(@"B Service %@ could not be resolved", netService.name);

    [self returnResultWithError];
}

@end
