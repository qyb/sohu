import settings

DATABASE_ENGINE = 'django.db.backends.mysql'
DATABASE_NAME = 'sohupocketlib'
DATABASE_USER = 'sohukan'
DATABASE_PASSWORD = 'sohukan'
if  settings.IS_PRODUCTION_SERVER:
    DATABASE_HOST = '10.10.69.53'
else:
    DATABASE_HOST = ''
DATABASE_PORT = ''

if  settings.IS_PRODUCTION_SERVER:
    BROKER_HOST = "10.10.69.53"
else:
    BROKER_HOST = "localhost"
BROKER_PORT = 5672
BROKER_USER = "sohukan"
BROKER_PASSWORD = "starrynight"
BROKER_VHOST = "sohukan"