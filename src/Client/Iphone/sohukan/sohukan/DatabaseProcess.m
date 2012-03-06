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

- (id) init
{
    if ((self = [super init]))
    {   
        _db = [self connectionDB];
    }   
    return self;
}


- (FMDatabase *) connectionDB
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

- (void) createUserTable
{
    [_db executeUpdate:@"CREATE TABLE IF NOT EXISTS User (username TEXT PRIMARY KEY, login_time DATETIME)"];
}

- (void) createArticleTable
{   
    [_db executeUpdate:@"CREATE TABLE IF NOT EXISTS Article (key TEXT PRIMARY KEY, userid INTEGER, title TEXT, url TEXT, download_url TEXT, category TEXT, create_time DATETIME, is_star INTEGER, is_read INTEGER, is_download INTEGER)"];

}

- (void) createImageTable
{   
    [_db executeUpdate:@"CREATE TABLE IF NOT EXISTS Image (key TEXT, url TEXT NOT NULL, is_download INTEGER)"];

}

- (void) createCategoryTable
{
    [_db executeUpdate:@"CREATE TABLE IF NOT EXISTS Category (name TEXT PRIMARY KEY)"];
}

- (void) createOperationTable
{
    [_db executeUpdate:@"CREATE TABLE IF NOT EXISTS Operation (type TEXT PRIMARY KEY, method TEXT, content TEXT)"];
}

- (void) createHistoryTable
{
    [_db executeUpdate:@"CREATE TABLE IF NOT EXISTS History (key TEXT, read_time DATETIME)"];
}

- (void) insertArticleData:(NSMutableDictionary *)data userId:(int)userid
{
    NSString *category;
    if ([[[data objectForKey:@"category"] stringValue] length] == 0) {
        category = @"";
    }else{
        category = [[data objectForKey:@"category"] stringValue];
    }
    NSString *sql = [NSString stringWithFormat:@"INSERT INTO Article (key, userid, title, url, download_url, category, create_time, is_star, is_read, is_download) VALUES ('%@', %d, '%@', '%@', '%@', '%@', '%@', %@, %@, %d)", [data objectForKey:@"key"], userid, [data objectForKey:@"title"], [data objectForKey:@"url"], [data objectForKey:@"download_url"], category, [data objectForKey:@"create_time"], [data objectForKey:@"is_star"], [data objectForKey:@"is_read"], 0];
    [_db executeUpdate:sql];
}

- (void) executeUpdate:(NSString *)sql
{
    [_db executeUpdate:sql];
}

- (FMResultSet *) getRowEntity:(NSString *)tableName primaryKey:(NSString *)key
{
    NSString *sql = [NSString stringWithFormat:@"SELECT * FROM %@ WHERE key='%@'", tableName, key];
    FMResultSet *rs = [_db executeQuery:sql];
    return rs;
}

- (int) getUserId:(NSString *)username;
{   NSString *sql = [NSString stringWithFormat:@"SELECT rowid FROM User WHERE username='%@'", username];
    int res = [_db intForQuery:sql];
    if (res){
        return res;
    }else{
        return 0;
    }
}

- (Article *) getArticleInstance:(FMResultSet *)rs dateFormatter:(NSDateFormatter *)formatter owner:(Article *)article
{
    article.key = [rs stringForColumn:@"key"];
    article.title = [rs stringForColumn:@"title"];
    article.url = [rs stringForColumn:@"url"];
    article.download_url = [rs stringForColumn:@"download_url"];
    article.category = [rs stringForColumn:@"category"];
    article.create_time = [formatter dateFromString:[rs stringForColumn:@"create_time"]];
    article.is_star = [rs boolForColumn:@"is_star"];
    article.is_read = [rs boolForColumn:@"is_read"];
    article.is_download = [rs boolForColumn:@"is_download"];
    return article;
}

- (NSMutableArray *) getReadedArticles
{
    FMResultSet *rs = [_db executeQuery:@"SELECT * FROM Article WHERE is_read=1 ORDER BY create_time desc;"];
    NSMutableArray *articles = [[[NSMutableArray alloc] init] autorelease];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    while ([rs next]){
        Article *article = [[Article alloc] init];
        [articles addObject:[self getArticleInstance:rs dateFormatter:dateFormatter owner:article]];
        [article release];
    }
    [dateFormatter release];
    return articles;
}



- (NSMutableArray *) getNotReadArticles
{
    FMResultSet *rs = [_db executeQuery:@"SELECT * FROM Article WHERE is_read=0 ORDER BY create_time desc;"]; 
    NSMutableArray *articles = [[[NSMutableArray alloc] init] autorelease];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    while ([rs next]){
        Article *article = [[Article alloc] init];
        [articles addObject:[self getArticleInstance:rs dateFormatter:dateFormatter owner:article]];
        [article release];
    }
    [dateFormatter release];
    return articles;
}


- (NSMutableArray *) getAllCategory
{
    NSMutableArray *categorys = [[[NSMutableArray alloc] init] autorelease];
    FMResultSet *rs = [_db executeQuery:@"SELECT * FROM Category"];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    while ([rs next]) {
        Category *category = [[Category alloc] init];
        category.name = [rs stringForColumn:@"name"];
        NSString *sql = [NSString stringWithFormat:@"SELECT * FROM Article WHERE category='%@'", [rs stringForColumn:@"name"]];
        FMResultSet *qs = [_db executeQuery:sql];
        NSMutableArray *articles = [[NSMutableArray alloc] init];
        while ([qs next]){
            Article *article = [[Article alloc] init];
            [articles addObject:[self getArticleInstance:qs dateFormatter:dateFormatter owner:article]];
            [article release];
        }
        category.articles = articles;
        [categorys addObject:category];
        [category release];
        [articles release];
    }
    [rs close];
    [dateFormatter release];
    return categorys;
}

- (NSMutableArray *) getAllImage:(NSString *)key
{
    NSString *sql = [NSString stringWithFormat:@"SELECT * FROM Image WHERE key='%@'",key];
    FMResultSet *rs = [_db executeQuery:sql];
    NSMutableArray *images = [[[NSMutableArray alloc] init] autorelease];
    while ([rs next]){
        [images addObject:[rs stringForColumn:@"url"]];
    }
    return images;

}

- (NSMutableArray *) getRecentArticles
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    NSMutableArray *articles = [[[NSMutableArray alloc] init] autorelease];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    FMResultSet *rs = [_db executeQuery:@"SELECT key FROM History ORDER BY read_time DESC limit 20"];
    while ([rs next]) {
        NSString *sql = [NSString stringWithFormat:@"SELECT * FROM Article WHERE key='%@'", [rs stringForColumn:@"key"]];
        FMResultSet *qs = [_db executeQuery:sql];
        while ([qs next]){
            Article *article = [[Article alloc] init];
            [articles addObject:[self getArticleInstance:qs dateFormatter:dateFormatter owner:article]];
            [article release];
        }
    }
    [dateFormatter release];
    return articles;
}

- (NSMutableArray *) getHistoryList:(NSMutableArray *)array
{
    FMResultSet *rs = [_db executeQuery:@"SELECT * FROM History ORDER BY read_time DESC"];
    while ([rs next]) {
        [array addObject:[rs stringForColumn:@"key"]];
    }
    return array;
}

- (FMResultSet *) getCategoryEntity:(NSString *)name
{
    NSString *sql = [NSString stringWithFormat:@"SELECT * FROM Category WHERE name='%@'", name];
    FMResultSet *rs = [_db executeQuery:sql];
    return rs;
}

- (void)closeDB
{   
    [_db close];
}
@end
