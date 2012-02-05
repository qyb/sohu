# encoding: utf-8
import datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models

class Migration(SchemaMigration):

    def forwards(self, orm):
        
        # Adding model 'MyArticleInstance'
        db.create_table('article_myarticleinstance', (
            ('id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('user_id', self.gf('django.db.models.fields.IntegerField')()),
            ('key', self.gf('django.db.models.fields.CharField')(max_length=256)),
            ('title', self.gf('django.db.models.fields.CharField')(max_length=512)),
            ('url', self.gf('django.db.models.fields.URLField')(max_length=200)),
            ('is_read', self.gf('django.db.models.fields.BooleanField')(default=False)),
            ('cover', self.gf('django.db.models.fields.CharField')(max_length=128, blank=True)),
            ('is_star', self.gf('django.db.models.fields.BooleanField')(default=False)),
            ('is_delete', self.gf('django.db.models.fields.BooleanField')(default=False)),
            ('create_time', self.gf('django.db.models.fields.DateTimeField')(auto_now_add=True, blank=True)),
            ('read_time', self.gf('django.db.models.fields.DateTimeField')(null=True, blank=True)),
            ('delete_time', self.gf('django.db.models.fields.DateTimeField')(null=True, blank=True)),
            ('is_ready', self.gf('django.db.models.fields.BooleanField')(default=False)),
        ))
        db.send_create_signal('article', ['MyArticleInstance'])


    def backwards(self, orm):
        
        # Deleting model 'MyArticleInstance'
        db.delete_table('article_myarticleinstance')


    models = {
        'article.myarticleinstance': {
            'Meta': {'object_name': 'MyArticleInstance'},
            'cover': ('django.db.models.fields.CharField', [], {'max_length': '128', 'blank': 'True'}),
            'create_time': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            'delete_time': ('django.db.models.fields.DateTimeField', [], {'null': 'True', 'blank': 'True'}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'is_delete': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'is_read': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'is_ready': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'is_star': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'key': ('django.db.models.fields.CharField', [], {'max_length': '256'}),
            'read_time': ('django.db.models.fields.DateTimeField', [], {'null': 'True', 'blank': 'True'}),
            'title': ('django.db.models.fields.CharField', [], {'max_length': '512'}),
            'url': ('django.db.models.fields.URLField', [], {'max_length': '200'}),
            'user_id': ('django.db.models.fields.IntegerField', [], {})
        }
    }

    complete_apps = ['article']
