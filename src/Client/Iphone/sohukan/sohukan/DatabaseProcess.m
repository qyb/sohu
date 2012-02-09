//
//  DatabaseProcess.m
//  sohukan
//
//  Created by riven on 12-2-2.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import "DatabaseProcess.h"
#import "Models.h"

@implementation DatabaseProcess

-(id) init
{
    if ((self = [super init]))
    {   
        _db = [self connectionDB];
    }   
    return self;
}


-(FMDatabase *)connectionDB
{    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);  
    NSString *documentDirectory = [paths objectAtIndex:0];  
    NSString *dbPath = [documentDirectory stringByAppendingPathComponent:@"sohukan.db"];  
    FMDatabase *db= [FMDatabase databaseWithPath:dbPath] ;  
    if (![db open]) {  
        NSLog(@"Could not open db.");
        return nil;
    }else{
        return db;
    } 
}

-(BOOL)tableExist:(NSString *)name
{
    if([_db tableExists:name]){
        return YES;
    }else{
        return NO;
    }
}


-(void)createArticleTable
{   
    [_db executeUpdate:@"CREATE TABLE IF NOT EXISTS Article (key TEXT, title TEXT, url TEXT, download_url TEXT, cover TEXT, is_star INTEGER, is_read INTEGER, is_download INTEGER, is_delete INTEGER)"];

}

-(void)createImageTable
{   
    [_db executeUpdate:@"CREATE TABLE IF NOT EXISTS Image (key TEXT, url TEXT, is_download INTEGER)"];

}

-(void)createCategoryTable
{
    [_db executeUpdate:@"CREATE TABLE IF NOT EXISTS Category (name TEXT)"];
}

-(void)insertArticleData:(NSMutableDictionary *)data
{
    NSString *sql = [NSString stringWithFormat:@"INSERT INTO Article (key, title, url, download_url, img_urls, cover, is_star, is_read, is_download, is_delete) VALUES ('%@', '%@', '%@', '%@', '%@', %@, %@, %d, %d)", [data objectForKey:@"key"], [data objectForKey:@"title"], [data objectForKey:@"url"], [data objectForKey:@"download_url"], [data objectForKey:@"cover"], [data objectForKey:@"is_star"], [data objectForKey:@"is_read"], 0, 0];
    [_db executeUpdate:sql];
}

-(void)insertImageData:(NSMutableDictionary *)data
{
    NSString *sql = [NSString stringWithFormat:@"INSERT INTO Image (key, url, is_download) VALUES ('%@', '%@', %d)", [data objectForKey:@"key"], [data objectForKey:@"url"], 0];
    [_db executeQuery:sql];

}

-(void)insertCategoryData:(NSMutableDictionary *)data
{
    NSString *sql = [NSString stringWithFormat:@"INSERT INTO Category (name) VALUES ('%@')", [data objectForKey:@"name"]];
    [_db executeQuery:sql];
}

-(void)updateBoolData:(NSString *)tableName primaryKey:(NSString *)key columnName:(NSString *)name setValue:(int)value
{
    NSString *sql = [NSString stringWithFormat:@"UPDATE %@ SET %@ = %@ WHERE key= %@",tableName, name, value, key];
    [_db executeUpdate:sql];

}

-(void)updateStringData:(NSString *)tableName primaryKey:(NSString *)key columnName:(NSString *)name setValue:(NSString *)value
{
    NSString *sql = [NSString stringWithFormat:@"UPDATE Article SET %@ = '%@' WHERE key= %@",tableName, value, key];
    [_db executeUpdate:sql];
}

-(BOOL)articleExist:(NSString *)key
{
    NSString *article_key =[_db stringForQuery:@"SELECT key FROM Article WHERE key = ?", key];
    if (article_key){
        return YES;
    }else{
        return NO;
    }

}

-(NSMutableArray *) getReadedArticles
{
    NSMutableArray *articles = [[[NSMutableArray alloc] init] autorelease];
    FMResultSet *rs = [_db executeQuery:@"SELECT * FROM Article WHERE is_read=1 AND is_download=1 AND is_delete=0"];
    while ([rs next]){
        Article *article = [[Article alloc] init];
        article.key = [rs stringForColumn:@"key"];
        article.title = [rs stringForColumn:@"title"];
        article.url = [rs stringForColumn:@"url"];
        article.download_url = [rs stringForColumn:@"download_url"];
        article.cover = [rs stringForColumn:@"cover"];
        article.category = [rs stringForColumn:@"category"];
        article.is_star = [rs boolForColumn:@"is_star"];
        article.is_read = [rs boolForColumn:@"is_read"];
        article.is_download = [rs boolForColumn:@"is_download"];
        article.is_delete = [rs boolForColumn:@"is_delete"];
        [articles addObject:article];
        [article release];
    }
    [rs close];
    return articles;
}

-(NSMutableArray *) getNotReadArticles
{
    NSMutableArray *articles = [[[NSMutableArray alloc] init] autorelease];
    FMResultSet *rs = [_db executeQuery:@"SELECT * FROM Article WHERE is_read=0 AND is_download=1 AND is_delete=0"]; 
    while ([rs next]){
        Article *article = [[Article alloc] init];
        article.key = [rs stringForColumn:@"key"];
        article.title = [rs stringForColumn:@"title"];
        article.url = [rs stringForColumn:@"url"];
        article.download_url = [rs stringForColumn:@"download_url"];
        article.cover = [rs stringForColumn:@"cover"];
        article.category = [rs stringForColumn:@"category"];
        article.is_star = [rs boolForColumn:@"is_star"];
        article.is_read = [rs boolForColumn:@"is_read"];
        article.is_download = [rs boolForColumn:@"is_download"];
        article.is_delete = [rs boolForColumn:@"is_delete"];
        [articles addObject:article];
        [article release];
    }
    [rs close];
    return articles;

}

- (void)closeDB
{   
    [_db close];
}
@end
