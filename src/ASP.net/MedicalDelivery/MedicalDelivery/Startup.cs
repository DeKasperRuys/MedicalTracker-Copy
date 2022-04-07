using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using MedicalDelivery.Services;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.HttpsPolicy;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using Microsoft.OpenApi.Models;
using AutoMapper;
using MedicalDelivery.Helpers;
using Microsoft.EntityFrameworkCore;

namespace MedicalDelivery
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddCors();
            services.AddMvc().SetCompatibilityVersion(CompatibilityVersion.Version_2_2);
            services.AddSwaggerGen(c =>
            {
                c.SwaggerDoc("v1", new OpenApiInfo { Title = "Medical Delivery API", Version = "v1" });
            });
            services.AddDbContext<LibraryContext>(
                    options => options.UseSqlServer(
                    Configuration.GetConnectionString("DefaultConnection")
));
            services.AddAutoMapper(typeof(Startup));

            services.AddScoped<IHospitalService, HospitalService>();
            services.AddScoped<IMedicineService, MedicineService>();
            services.AddScoped<IDeliveryService, DeliveryService>();
            services.AddScoped<IRiderService, RiderService>();
            services.AddScoped<IUpdateService, UpdateService>();
            services.AddScoped<IPayloadService, PayloadService>();

        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IHostingEnvironment env, LibraryContext libraryContext)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }
            else
            {
                // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
                app.UseHsts();
            }

            app.UseHttpsRedirection();
            app.UseMvc();
            app.UseSwagger();
            app.UseSwaggerUI(c =>
            {
                c.SwaggerEndpoint("/swagger/v1/swagger.json", "Medical Delivery API V1");
            });
            app.UseCors("AllowAll");
            dbInitializer.Initialize(libraryContext);
            app.UseCors(x => x
            .AllowAnyOrigin()
            .AllowAnyMethod()
            .AllowAnyHeader());
        }
    }
}
