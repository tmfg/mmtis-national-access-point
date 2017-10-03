# -*- coding: utf-8 -*-
from setuptools import setup, find_packages

version = '1.0.0'

setup(
    name='ckanext-napote_theme',
    version=version,
    description="NAPOTE theme CKAN extension",
    long_description="""
    """,

    # https://pypi.python.org/pypi?%3Aaction=list_classifiers
    classifiers=[],
    keywords='',
    author='Solita Ltd.',
    author_email='',
    url='',

    # You can specify the packages manually here if your project is simple. Or you can use find_packages().
    packages=find_packages(exclude=['']),
    namespace_packages=['ckanext'],
    include_package_data=True,
    package_data={},
    zip_safe=False,

    # Run time dependencies.
    # These will be installed by pip when your project is installed.
    install_requires=[
        # Extra requirements:
    ],
    entry_points="""
    [ckan.plugins]
    napote_theme=ckanext.napote_theme.plugin:NapoteThemePlugin
    """,
)
