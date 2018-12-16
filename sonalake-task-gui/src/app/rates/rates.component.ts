import { Component, OnInit } from '@angular/core';
import { Rate } from '../rate';
import { RateService } from '../rate.service';

@Component({
    selector: 'app-rates',
    templateUrl: './rates.component.html',
    styleUrls: ['./rates.component.css']
})
export class RatesComponent implements OnInit {
    rates: Rate[] = [new Rate('UK', 'GBP'), new Rate('DE', 'EUR'), new Rate('PL', 'PLN')];
    responseRates: Rate[];

    // rates = RATES;
    constructor(private rateService: RateService) { }

    getRates(): void {
        this.rateService.getRates(this.rates)
            .subscribe(responseRates => this.responseRates = responseRates);
    }

    ngOnInit() {
        // this.getRates();
    }

}
