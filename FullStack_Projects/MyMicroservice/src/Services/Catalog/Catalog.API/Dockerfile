# ---- 构建阶段 ----
    FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
    WORKDIR /app
    
    # 拷贝项目文件并还原依赖
    COPY *.csproj ./
    RUN dotnet restore
    
    # 拷贝源代码并构建
    COPY . ./
    RUN dotnet publish -c Release -o /out
    
    # ---- 运行阶段 ----
    FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS runtime
    WORKDIR /app
    COPY --from=build /out .
    
    EXPOSE 80
    ENTRYPOINT ["dotnet", "Catalog.API.dll"]