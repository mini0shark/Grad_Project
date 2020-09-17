import os
import RecipeServer.open_file as of

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


SECRET_KEY = of.get_string_from_file('/home/ubuntu/keyString/SECRET_KEY.txt')
DEBUG = True
STATIC_URL = '/static/'
project = 'Recipe'
ALLOWED_HOSTS = ['*']


INSTALLED_APPS = [
    'channels',
    'chatting.apps.ChattingConfig',
    'userApi.apps.UserApiConfig',
    'recipeApi.apps.RecipeApiConfig',
    'storages',
    'django.contrib.admin',
    'django.contrib.auth',  # 인증 시스템
    'django.contrib.contenttypes',  # 컨텐츠 타입을 위한 프레임 워크
    'django.contrib.sessions',  # 세션 프레임 워크
    'django.contrib.messages',  # 메세징 프레임 워크
    'django.contrib.staticfiles',  # 정적 파일을 관리하는 프레임 워크
    'rest_framework',
    'drf_yasg',
]
ASGI_APPLICATION = 'RecipeServer.routing.application'
CHANNEL_LAYERS = {
    'default': {
        'BACKEND': 'channels_redis.core.RedisChannelLayer',
        'CONFIG': {
            "hosts": [('127.0.0.1', 6379)],
        },
    },
}

MIDDLEWARE = [
    'django.middleware.security.SecurityMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
]


ROOT_URLCONF = 'RecipeServer.urls'

TEMPLATES = [
    {
        'BACKEND': 'django.template.backends.django.DjangoTemplates',
        'DIRS': [],
        'APP_DIRS': True,
        'OPTIONS': {
            'context_processors': [
                'django.template.context_processors.debug',
                'django.template.context_processors.request',
                'django.contrib.auth.context_processors.auth',
                'django.contrib.messages.context_processors.messages',
                'django.template.context_processors.request',
            ],
        },
    },
]

WSGI_APPLICATION = 'RecipeServer.wsgi.application'


host =of.get_string_from_file('/home/ubuntu/keyString/DB_HOST.txt').strip()
usr = of.get_string_from_file('/home/ubuntu/keyString/DB_USER.txt').strip()
pwd = of.get_string_from_file('/home/ubuntu/keyString/DB_PASSWORD.txt').strip()
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'HOST': host,
        'PORT': '3306',
        'NAME': 'footory',
        'USER': usr,
        'PASSWORD': pwd,
    }
}


AUTH_PASSWORD_VALIDATORS = [
    {
        'NAME': 'django.contrib.auth.password_validation.UserAttributeSimilarityValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.MinimumLengthValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.CommonPasswordValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.NumericPasswordValidator',
    },
]


LANGUAGE_CODE = 'en-us'

TIME_ZONE = 'Asia/Seoul'

USE_I18N = True

USE_L10N = True

USE_TZ = True



REST_FRAMEWORK = {
    'DEFAULT_AUTHENTICATION_CLASSES': (
        'rest_framework.authentication.TokenAuthentication',
    )
}
STATICFILES_DIRS = (
    os.path.join(BASE_DIR, "uploads"),  # Root의 static 파일
    '/recipeApi/uploads/',  # garden App의 static 파일
)

MEDIA_URL = '/media/'
MEDIA_ROOT = os.path.join(BASE_DIR, 'media')

AUTH_USER_MODEL = "userApi.User"

AWS_ACCESS_KEY_ID = of.get_string_from_file('/home/ubuntu/keyString/AWS_ACCESS_KEY_ID.txt')
AWS_SECRET_ACCESS_KEY = of.get_string_from_file('/home/ubuntu/keyString/AWS_SECRET_ACCESS_KEY.txt')
AWS_REGION = 'ap-northeast-2'  # 아시아 태평양(서울)
AWS_STORAGE_BUCKET_NAME = 'mini0shark'
AWS_S3_CUSTOM_DOMAIN = '%s.s3.%s.amazonaws.com' % (AWS_STORAGE_BUCKET_NAME, AWS_REGION)
AWS_S3_OBJECT_PARAMETERS = {
    'CacheControl': 'max-age=86400',
}
DEFAULT_FILE_STORAGE = 'RecipeServer.asset_storage.MediaStorage'
