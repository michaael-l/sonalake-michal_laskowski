import { Injectable } from '@angular/core';
import { Rate } from './rate';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class RateService {

    private getPayUrl = 'http://localhost:8080/getPay';

    getRates(rates: Rate[]): Observable<Rate[]> {
        const httpOptions = {
            headers: new HttpHeaders({ 'Content-Type': 'application/json' })
        };
        return this.http.post<Rate[]>(this.getPayUrl, rates, httpOptions);
    }

    constructor(private http: HttpClient) { }
}
