/*
DownloadOperation.m

Author: Makoto Kinoshita

Copyright 2009 HMDT. All rights reserved.
*/

#import "DownloadOperation.h"

@implementation DownloadOperation
@synthesize data = _data;
@synthesize key = _key;
@synthesize type = _type;

+ (BOOL)automaticallyNotifiesObserversForKey:(NSString*)key
{
	if ([key isEqualToString:@"isExecuting"] || 
        [key isEqualToString:@"isFinished"])
    {
		return YES;
	}
	
	return [super automaticallyNotifiesObserversForKey:key];
}

- (id)initWithRequest:(NSURLRequest*)request
{
    if (![super init]) {
        return nil;
    }
    _request = [request retain];
    _data = [[NSMutableData data] retain];
	_isExecuting = NO;
	_isFinished = NO;
    
    return self;
}



- (void)dealloc
{
    [_request release], _request = nil;
	[_connection cancel], [_connection release], _connection = nil;
	[_data release], _data = nil;
    [_type release];
    [_key release];
    
	[super dealloc];
}

- (BOOL)isConcurrent
{
	return YES;
}

- (BOOL)isExecuting
{
	return _isExecuting;
}

- (BOOL)isFinished
{
	return _isFinished;
}

- (void)start
{  
	if (![self isCancelled]) {
        [self setValue:[NSNumber numberWithBool:YES] forKey:@"isExecuting"];
		_connection = [[NSURLConnection connectionWithRequest:_request delegate:self]retain];
        while(_connection != nil) {
            [[NSRunLoop currentRunLoop] runMode:NSDefaultRunLoopMode beforeDate:[NSDate distantFuture]];
        }
    }
}

- (void)cancel
{   
	[_connection cancel], _connection = nil;
    [self setValue:[NSNumber numberWithBool:NO] forKey:@"isExecuting"];
    [self setValue:[NSNumber numberWithBool:YES] forKey:@"isFinished"];
	[super cancel];
}


- (void)connection:(NSURLConnection*)connection
		didReceiveData:(NSData*)data
{
    [_data appendData:data];
}

- (void)connectionDidFinishLoading:(NSURLConnection*)connection
{
    _connection = nil;
    [self setValue:[NSNumber numberWithBool:NO] forKey:@"isExecuting"];
    [self setValue:[NSNumber numberWithBool:YES] forKey:@"isFinished"];
}

- (void)connection:(NSURLConnection*)connection
		didFailWithError:(NSError*)error
{   
    _connection = nil;
    [self setValue:[NSNumber numberWithBool:NO] forKey:@"isExecuting"];
    [self setValue:[NSNumber numberWithBool:YES] forKey:@"isFinished"];
}
@end
